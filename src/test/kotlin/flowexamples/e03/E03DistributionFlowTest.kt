package flowexamples.e03

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.greenbird.metercloud.integration.flow.spec.dsl.RestRequestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.FlowEditor.map
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.backends.wiremock.WireMockTestBackend.withWireMock
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.flowdeveloper.sdk.utils.rest.basicAuth
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestUtils.assertOrTimeout
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.e03.E03DistributionFlow.distributionApiSpec
import flowexamples.e03.E03DistributionFlow.distributionOpenApiDefinition
import flowexamples.e03.E03DistributionFlow.distributionRestResourceKey
import flowexamples.e03.E03DistributionFlow.distributionTarget1Spec
import flowexamples.e03.E03DistributionFlow.distributionTarget2Spec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.Duration.ofSeconds
import javax.ws.rs.client.Entity.json

class E03DistributionFlowTest : ConcurrentTestBase() {
    private val TIMEOUT_DURATION = ofSeconds(5)

    @Test
    fun `E03 GIVEN deployed distribution flows WHEN sending a value THEN the message is distributed to the two target flows`(
        ctx: ConcurrentTestContext
    ) {
        val inputValue = "testValue"

        withWireMock { wireMockServer ->
            wireMockServer.stubFor(post(urlPathEqualTo("/target1")))
            wireMockServer.stubFor(post(urlPathEqualTo("/target2")))

            val wireMockBaseUrl = "http://localhost:${wireMockServer.port()}"

            val mockedTarget1Flow = distributionTarget1Spec.map<RestRequestConfig> { restRequestConfig ->
                restRequestConfig.copy(address = URL("$wireMockBaseUrl/target1"))
            }
            val mockedTarget2Flow = distributionTarget2Spec.map<RestRequestConfig> { restRequestConfig ->
                restRequestConfig.copy(address = URL("$wireMockBaseUrl/target2"))
            }

            val openApiResource = Resource(key = distributionRestResourceKey, content = distributionOpenApiDefinition)
            ctx.addFlowTestConfig {
                resource(openApiResource)
                flow(distributionApiSpec)
                flow(mockedTarget1Flow)
                flow(mockedTarget2Flow)
            }

            flowTest(ctx) {
                val responseMessage = restApiEndpoint(distributionApiSpec)
                    .path("echo")
                    .request()
                    .basicAuth()
                    .post(json(SimpleValue(inputValue)), SimpleMessage::class.java)

                // Verify the response from the parent flow
                assertThat(responseMessage.message).isEqualTo("Distributed")

                assertOrTimeout(TIMEOUT_DURATION) {
                    // Verify that the handoff flow made a REST request with the given payload
                    wireMockServer.verify(
                        postRequestedFor(urlEqualTo("/target1"))
                            .withRequestBody(equalToJson(""" { "value": "$inputValue" } """))
                    )

                    wireMockServer.verify(
                        postRequestedFor(urlEqualTo("/target2"))
                            .withRequestBody(equalToJson(""" { "value": "$inputValue" } """))
                    )
                }
            }
        }
    }

}
