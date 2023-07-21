package flowexamples.e03

import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.OneWay
import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.HttpMethod.POST
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceRevisionKey.Companion.newResourceRevisionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath
import java.net.URL

object E03DistributionFlow {
    private const val FLOW_ID_API = "distribution-api"
    private const val FLOW_ID_TARGET_1 = "distribution-target-1-handoff"
    private const val FLOW_ID_TARGET_2 = "distribution-target-2-handoff"

    val distributionOpenApiDefinition = fromClasspath("/echo-open-api-v1.json")

    val distributionRestResourceKey = newResourceRevisionKey {
        ownerId = OWNER_ID
        type = "OpenAPIv3"
        id = "distribution-api"
        revision = "latest"
    }

    val distributionApiSpec = flowConfig {
        id = FLOW_ID_API
        description = "Distribution Flow"
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

        map {
            id = "create-response"
            mapSpec = """
                {
                    "message" : "Distributed"
                }
            """.trimIndent()
        }

    }

    val distributionTarget1Spec = flowConfig {
        id = FLOW_ID_TARGET_1
        description = "Distribution Target Flow"
        ownerId = OWNER_ID
        exchangePattern = OneWay

        handoff { id = "handoff-source-1" }

        restRequest {
            id = "request-handoff-1"
            defaultMethod = POST
            address = URL("https://OVERRIDE_ME/target1")
        }

    }

    val distributionTarget2Spec = flowConfig {
        id = FLOW_ID_TARGET_2
        description = "Distribution Target Flow"
        ownerId = OWNER_ID
        exchangePattern = OneWay

        handoff { id = "handoff-source-2" }

        restRequest {
            id = "request-handoff-2"
            defaultMethod = POST
            address = URL("https://OVERRIDE_ME/target2")
        }

    }

}
