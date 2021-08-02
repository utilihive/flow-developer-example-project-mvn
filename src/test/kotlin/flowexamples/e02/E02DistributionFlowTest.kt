package flowexamples.e02

import com.greenbird.utilihive.integration.flowdeveloper.sdk.log.LoggerNames
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.server.UtilihiveRestClientFactory.basicAuth
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleMessage
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.entities.SimpleValue
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestManager
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import com.greenbird.utilihive.integration.test.concurrent.core.log.LogQuery.Companion.logQuery
import flowexamples.e02.E02DistributionFlow.distributionApiSpec
import flowexamples.e02.E02DistributionFlow.distributionOpenApiDefinition
import flowexamples.e02.E02DistributionFlow.distributionRestResourceKey
import flowexamples.e02.E02DistributionFlow.distributionTarget1Spec
import flowexamples.e02.E02DistributionFlow.distributionTarget2Spec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import javax.ws.rs.client.Entity.json

class E02DistributionFlowTest : ConcurrentTestBase() {
    private val logAsserter = ConcurrentTestManager.testLogAssertions

    @Disabled("Until test server deploy time issues are fixed")
    @Test
    fun `E02 GIVEN deployed distribution flows WHEN sending a value THEN the message is distributed to the two target flows`(
        ctx: ConcurrentTestContext
    ) {
        val openApiResource = Resource(key = distributionRestResourceKey, content = distributionOpenApiDefinition)
        ctx.addFlowTestConfig {
            resource(openApiResource)
            flow(distributionApiSpec)
            flow(distributionTarget1Spec)
            flow(distributionTarget2Spec)
        }

        flowTest(ctx) {
            val responseMessage = restApiEndpoint(distributionApiSpec)
                .path("echo")
                .request()
                .basicAuth()
                .post(json(SimpleValue("testValue")), SimpleMessage::class.java)

            assertThat(responseMessage.message).isEqualTo("Distributed")

            logAsserter.awaitEvent(
                logQuery {
                    logger = LoggerNames.TEST_PROCESSOR
                    flowId = flowId(distributionTarget1Spec)
                    messagePhrase("Processing")
                }
            )
            logAsserter.awaitEvent(
                logQuery {
                    logger = LoggerNames.TEST_PROCESSOR
                    flowId = flowId(distributionTarget2Spec)
                    messagePhrase("Processing")
                }
            )
        }
    }

}
