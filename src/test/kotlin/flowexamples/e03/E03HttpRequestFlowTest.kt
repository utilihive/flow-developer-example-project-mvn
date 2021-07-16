package flowexamples.e03

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.greenbird.metercloud.integration.flow.spec.dsl.RestRequestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.FlowEditor.map
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory.basicAuth
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory.flowRestEndpointAddress
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.common.wiremock.WireMockTestManager.withWireMock
import flowexamples.e01.E01SimpleRestFlow.simpleRestOpenApiDefinition
import flowexamples.e01.E01SimpleRestFlow.simpleRestResourceKey
import flowexamples.e01.E01SimpleRestFlow.simpleRestSpec
import flowexamples.e03.E03HttpRequestFlow.httpRequestOpenApiDefinition
import flowexamples.e03.E03HttpRequestFlow.httpRequestResourceKey
import flowexamples.e03.E03HttpRequestFlow.httpRequestSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.net.URL
import javax.ws.rs.client.Entity.json

class E03HttpRequestFlowTest : ConcurrentTestBase() {

    @Disabled("Until test server deploy time issues are fixed")
    @Test
    fun `E03-1 GIVEN backend stub flow WHEN sending a value THEN the request is forwarded to the stub flow and echoed back`(
        ctx: ConcurrentTestContext
    ) {
        val backendOpenApiResource = Resource(key = simpleRestResourceKey, content = simpleRestOpenApiDefinition)
        // we reuse the echo flow from E01 as our backend flow
        val backendFlow = simpleRestSpec


        val frontendApiResource = Resource(key = httpRequestResourceKey, content = httpRequestOpenApiDefinition)
        // We adjust the rest request target address to point to the backend stub flow
        val frontendFlow = httpRequestSpec.map<RestRequestConfig> { restRequestConfig ->
            restRequestConfig.copy(address = URL(flowRestEndpointAddress(backendFlow, path = "echo")))
        }

        ctx.addFlowTestConfig {
            resource(backendOpenApiResource)
            resource(frontendApiResource)
            flow(frontendFlow)
            flow(backendFlow)
        }

        flowTest(ctx) {
            val inputValue = "testValue"
            val responseMessage = restApiEndpoint(frontendFlow)
                .path("echo")
                .request()
                .basicAuth()
                .post(json(SimpleValue(inputValue)), SimpleMessage::class.java)
            assertThat(responseMessage.message).isEqualTo(inputValue)
        }
    }

    @Disabled("Until test server deploy time issues are fixed")
    @Test
    fun `E03-2 GIVEN wiremock backend stub WHEN sending a value THEN the request is forwarded to the stub flow and echoed back`(
        ctx: ConcurrentTestContext
    ) {
        val inputValue = "testValue"

        withWireMock { wireMockServer ->

            wireMockServer.stubFor(
                post(urlPathEqualTo("/echo"))
                    .withRequestBody(equalToJson(""" { "value": "$inputValue" } """))
                    .willReturn(okJson(""" { "message": "$inputValue" } """))
            )


            val frontendApiResource = Resource(key = httpRequestResourceKey, content = httpRequestOpenApiDefinition)
            // We adjust the rest request target address to point to the backend stub flow
            val frontendFlow = httpRequestSpec.map<RestRequestConfig> { restRequestConfig ->
                restRequestConfig.copy(address = URL("http://localhost:${wireMockServer.port()}/echo"))
            }

            ctx.addFlowTestConfig {
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
