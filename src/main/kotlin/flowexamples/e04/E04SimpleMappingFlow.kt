package flowexamples.e04

import com.greenbird.metercloud.integration.beanmapper.dsl.toDtoJson
import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceRevisionKey.Companion.newResourceRevisionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath
import flowexamples.e04.E04SimpleMapping.simpleMapping

object E04SimpleMappingFlow {
    val simpleMappingOpenApiDefinition = fromClasspath("/echo-open-api-v1.json")

    val simpleMappingResourceKey = newResourceRevisionKey {
        ownerId = OWNER_ID
        type = "OpenAPIv3"
        id = "simple-mapping-api"
        revision = "latest"
    }

    val simpleMappingSpec = flowConfig {
        id = "simple-mapping"
        name = id
        ownerId = OWNER_ID
        exchangePattern = RequestResponse

        restApi {
            id = "mapping-api"
            apiSpecId = simpleMappingResourceKey.toResourceIdentifier()
        }

        beanTransformer {
            id = "map-response"
            transformerSpec = simpleMapping.toDtoJson() // include the serialized version of the mapping
        }
    }

}
