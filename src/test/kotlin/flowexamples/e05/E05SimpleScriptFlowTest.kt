package flowexamples.e05

import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.utils.rest.basicAuth
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.e05.E05SimpleScriptFlow.simpleScriptOpenApiDefinition
import flowexamples.e05.E05SimpleScriptFlow.simpleScriptResourceKey
import flowexamples.e05.E05SimpleScriptFlow.simpleScriptSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.ws.rs.client.Entity.json

class E05SimpleScriptFlowTest : ConcurrentTestBase() {

    @Test
    fun `E05 GIVEN deployed echo script flow WHEN sending a value THEN the value is echoed back`(ctx: ConcurrentTestContext) {
        val openApiResource = Resource(key = simpleScriptResourceKey, content = simpleScriptOpenApiDefinition)
        ctx.addFlowTestConfig { resource(openApiResource); flow(simpleScriptSpec) }

        flowTest(ctx) {
            val inputValue = "testValue"
            val responseMessage = restApiEndpoint(simpleScriptSpec)
                .path("echo")
                .request()
                .basicAuth()
                .post(json(SimpleValue(inputValue)), SimpleMessage::class.java)
            assertThat(responseMessage.message).isEqualTo(inputValue)
        }
    }

}
