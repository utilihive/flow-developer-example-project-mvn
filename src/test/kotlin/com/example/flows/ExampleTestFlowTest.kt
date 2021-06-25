package com.example.flows

import com.example.flows.ExampleTestFlow.exampleOpenApiDoc
import com.example.flows.ExampleTestFlow.exampleResourceKey
import com.example.flows.ExampleTestFlow.exampleSpec
import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.FlowEditor.forTestContext
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory.basicAuth
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.ws.rs.client.Entity

class ExampleTestFlowTest : ConcurrentTestBase() {

    @Test
    fun `WHEN deploying a flow THEN the server is available and the flow is deployed and working`(
        ctx: ConcurrentTestContext
    ) {
        val resource = Resource(key = exampleResourceKey, content = exampleOpenApiDoc)
        val flowSpec = exampleSpec.forTestContext(ctx)
        ctx.addFlowTestConfig { resource(resource); flow(flowSpec) }

        FlowTestManager.flowTest(ctx) {
            val value = "testValue"
            val responseMessage = UtilihiveRestClientFactory.flowRestEndpoint(flowSpec).path("echo").request()
                .basicAuth().post(Entity.json(SimpleValue(value)), SimpleMessage::class.java)
            assertThat(responseMessage.message).isEqualTo(value)
        }
    }

}
