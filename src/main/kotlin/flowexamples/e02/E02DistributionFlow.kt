package flowexamples.e02

import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.OneWay
import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceVersionKey.Companion.newResourceVersionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath

object E02DistributionFlow {
    private const val FLOW_ID_API = "distribution-api"
    private const val FLOW_ID_TARGET_1 = "distribution-target-1-handoff"
    private const val FLOW_ID_TARGET_2 = "distribution-target-2-handoff"

    val distributionOpenApiDefinition = fromClasspath("/echo-open-api-v1.json")

    val distributionRestResourceKey = newResourceVersionKey {
        ownerId = OWNER_ID
        type = "OpenAPIv3"
        id = "distribution-api"
        version = "v1"
    }

    val distributionApiSpec = flowConfig {
        id = FLOW_ID_API
        name = id
        ownerId = OWNER_ID
        exchangePattern = RequestResponse

        restApi {
            id = "distribution-api"
            apiSpecId = distributionRestResourceKey.toResourceIdentifier()
        }

        distribute {
            id = "distribute-message"
            flowId(FLOW_ID_TARGET_1)
            flowId(FLOW_ID_TARGET_2)
        }

        executeScript {
            id = "create-response"
            language = "JSON"
            script = """
                return {
                    message : 'Distributed'
                }
                """.trimIndent()
        }

    }

    val distributionTarget1Spec = flowConfig {
        id = FLOW_ID_TARGET_1
        name = id
        ownerId = OWNER_ID
        exchangePattern = OneWay

        handoff { id = "handoff-source-1" }

        test {
            id = "log-processing-1"
            messageLoggingStrategy { logDescription = true }
        }

    }

    val distributionTarget2Spec = flowConfig {
        id = FLOW_ID_TARGET_2
        name = id
        ownerId = OWNER_ID
        exchangePattern = OneWay

        handoff { id = "handoff-source-2" }

        test {
            id = "log-processing-2"
            messageLoggingStrategy { logDescription = true }
        }

    }

}
