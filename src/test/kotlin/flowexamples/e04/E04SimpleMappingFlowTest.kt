package flowexamples.e04

import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory.basicAuth
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.e04.E04SimpleMappingFlow.simpleMappingOpenApiDefinition
import flowexamples.e04.E04SimpleMappingFlow.simpleMappingResourceKey
import flowexamples.e04.E04SimpleMappingFlow.simpleMappingSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.ws.rs.client.Entity.json

class E04SimpleMappingFlowTest : ConcurrentTestBase() {

    @Test
    fun `E04 GIVEN deployed echo mapping flow WHEN sending a value THEN the value is echoed back`(ctx: ConcurrentTestContext) {
        val openApiResource = Resource(key = simpleMappingResourceKey, content = simpleMappingOpenApiDefinition)
        ctx.addFlowTestConfig { resource(openApiResource); flow(simpleMappingSpec) }

        flowTest(ctx) {
            val inputValue = "testValue"
            val responseMessage = restApiEndpoint(simpleMappingSpec)
                .path("echo")
                .request()
                .basicAuth()
                .post(json(SimpleValue(inputValue)), SimpleMessage::class.java)
            assertThat(responseMessage.message).isEqualTo(inputValue)
        }
    }

}
