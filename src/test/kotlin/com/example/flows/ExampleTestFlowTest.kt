package com.example.flows

import com.example.flows.ExampleTestFlow.exampleOpenApiDoc
import com.example.flows.ExampleTestFlow.exampleResourceKey
import com.example.flows.ExampleTestFlow.exampleSpec
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory.basicAuth
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.ws.rs.client.Entity.json

class ExampleTestFlowTest : ConcurrentTestBase() {

    @Test
    fun `WHEN deploying a flow THEN the server is available and the flow is deployed and working`(
        ctx: ConcurrentTestContext
    ) {
        val resource = Resource(key = exampleResourceKey, content = exampleOpenApiDoc)
        ctx.addFlowTestConfig { resource(resource); flow(exampleSpec) }

        FlowTestManager.flowTest(ctx) {
            val value = "testValue"
            val responseMessage = restApiEndpoint(exampleSpec).path("echo").request()
                .basicAuth().post(json(SimpleValue(value)), SimpleMessage::class.java)
            assertThat(responseMessage.message).isEqualTo(value)
        }
    }

}
