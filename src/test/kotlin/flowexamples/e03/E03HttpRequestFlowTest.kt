package flowexamples.e03

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.greenbird.metercloud.integration.flow.spec.dsl.RestRequestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.FlowEditor.map
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory.basicAuth
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.backends.wiremock.WireMockTestBackend.withWireMock
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.e03.E03HttpRequestFlow.httpRequestOpenApiDefinition
import flowexamples.e03.E03HttpRequestFlow.httpRequestResourceKey
import flowexamples.e03.E03HttpRequestFlow.httpRequestSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URL
import javax.ws.rs.client.Entity.json

class E03HttpRequestFlowTest : ConcurrentTestBase() {

    @Test
    fun `E03-2_1 GIVEN wiremock backend stub WHEN using classpath-provided auth and sending a value THEN the request is forwarded to the stub flow and echoed back`(
        ctx: ConcurrentTestContext
    ) {
        verifyRestRequest(ctx)
    }

    @Test
    fun `E03-2_2 GIVEN wiremock backend stub WHEN using test-provided auth and sending a value THEN the request is forwarded to the stub flow and echoed back`(
        ctx: ConcurrentTestContext
    ) {
        verifyRestRequest(ctx, useTestProvidedAuth = true)
    }

    private fun verifyRestRequest(ctx: ConcurrentTestContext, useTestProvidedAuth: Boolean = false) {
        val inputValue = "testValue"
        val username =
            if (useTestProvidedAuth) "testProvidedBackendUser" else "classpathProvidedBackendUser"
        val password =
            if (useTestProvidedAuth) "testProvidedBackendPwd" else "classpathProvidedBackendPwd"

        withWireMock { wireMockServer ->
            // The basic auth credentials will be automatically
            // loaded from the authConfig.properties classpath resource.
            wireMockServer.stubFor(
                post(urlPathEqualTo("/echo"))
                    .withRequestBody(equalToJson(""" { "value": "$inputValue" } """))
                    .withBasicAuth(username, password)
                    .willReturn(okJson(""" { "message": "$inputValue" } """))
            )

            val frontendApiResource = Resource(key = httpRequestResourceKey, content = httpRequestOpenApiDefinition)
            // We adjust the rest request target address to point to the backend stub flow
            val frontendFlow = httpRequestSpec.map<RestRequestConfig> { restRequestConfig ->
                val backendAddress = URL("http://localhost:${wireMockServer.port()}/echo")

                if (useTestProvidedAuth) {
                    restRequestConfig.copy(
                        address = backendAddress,
                        authenticationConfigKey = "backendWithTestProvidedAuth"
                    )
                } else {
                    restRequestConfig.copy(address = backendAddress)
                }
            }

            ctx.addFlowTestConfig {
                if (useTestProvidedAuth) {
                    authConfig(
                        "backendWithTestProvidedAuth", mapOf(
                            "userName" to "testProvidedBackendUser",
                            "pwd" to "testProvidedBackendPwd"
                        )
                    )
                }

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
