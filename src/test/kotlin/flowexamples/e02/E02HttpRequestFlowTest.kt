package flowexamples.e02

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
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.e02.E02HttpRequestFlow.BACKEND_AUTHENTICATION_KEY
import flowexamples.e02.E02HttpRequestFlow.httpRequestOpenApiDefinition
import flowexamples.e02.E02HttpRequestFlow.httpRequestResourceKey
import flowexamples.e02.E02HttpRequestFlow.httpRequestSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URL
import jakarta.ws.rs.client.Entity.json

class E02HttpRequestFlowTest : ConcurrentTestBase() {

    @Test
    fun `E02 GIVEN wiremock backend stub WHEN sending a value THEN an authenticated request is forwarded to the stub flow and echoed back`(
        ctx: ConcurrentTestContext
    ) {
        val inputValue = "testValue"
        val user = "backendUser"
        val pwd = "backendPwd"

        withWireMock { wireMockServer ->
            wireMockServer.stubFor(
                post(urlPathEqualTo("/echo"))
                    .withRequestBody(equalToJson(""" { "value": "$inputValue" } """))
                    // The credentials sent by the restRequest processor are defined for the test by the authConfig statement below.
                    .withBasicAuth(user, pwd)
                    .willReturn(okJson(""" { "message": "$inputValue" } """))
            )

            val frontendApiResource = Resource(key = httpRequestResourceKey, content = httpRequestOpenApiDefinition)
            // We adjust the rest request target address to point to the backend stub flow
            val frontendFlow = httpRequestSpec.map<RestRequestConfig> { restRequestConfig ->
                val backendAddress = URL("http://localhost:${wireMockServer.port()}/echo")
                restRequestConfig.copy(address = backendAddress)
            }

            ctx.addFlowTestConfig {
                authConfig(
                    BACKEND_AUTHENTICATION_KEY, mapOf(
                        "userName" to user,
                        "password" to pwd
                    )
                )

                resource(frontendApiResource)
                flow(frontendFlow)
            }

            flowTest(ctx) {
                val responseMessage = restApiEndpoint(frontendFlow)
                    .path("echo")
                    .request()
                    .basicAuth()
                    .post(json(SimpleValue(inputValue)), SimpleMessage::class.java)
                assertThat(responseMessage.message).isEqualTo(inputValue)
            }
        }
    }

}
