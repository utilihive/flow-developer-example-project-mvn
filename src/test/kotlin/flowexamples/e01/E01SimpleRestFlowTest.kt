package flowexamples.e01

import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.flowdeveloper.sdk.utils.rest.basicAuth
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.e01.E01SimpleRestFlow.simpleRestOpenApiDefinition
import flowexamples.e01.E01SimpleRestFlow.simpleRestResourceKey
import flowexamples.e01.E01SimpleRestFlow.simpleRestSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import jakarta.ws.rs.client.Entity.json

class E01SimpleRestFlowTest : ConcurrentTestBase() {

    @Test
    fun `E01 GIVEN deployed echo flow WHEN sending a value THEN the value is echoed back`(ctx: ConcurrentTestContext) {
        val openApiResource = Resource(key = simpleRestResourceKey, content = simpleRestOpenApiDefinition)
        ctx.addFlowTestConfig { resource(openApiResource); flow(simpleRestSpec) }

        flowTest(ctx) {
            val inputValue = "testValue"
            val responseMessage = restApiEndpoint(simpleRestSpec)
                .path("echo")
                .request()
                .basicAuth()
                .post(json(SimpleValue(inputValue)), SimpleMessage::class.java)
            assertThat(responseMessage.message).isEqualTo(inputValue)
        }
    }

}
