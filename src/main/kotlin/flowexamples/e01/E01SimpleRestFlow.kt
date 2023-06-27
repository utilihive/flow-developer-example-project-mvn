package flowexamples.e01

import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceRevisionKey.Companion.newResourceRevisionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath

object E01SimpleRestFlow {
    val simpleRestOpenApiDefinition = fromClasspath("/echo-open-api-v1.json")

    val simpleRestResourceKey = newResourceRevisionKey {
        ownerId = OWNER_ID
        type = "OpenAPIv3"
        id = "simple-rest-api"
        revision = "latest"
    }

    val simpleRestSpec = flowConfig {
        id = "simple-rest"
        description = "Simple REST Flow"
        ownerId = OWNER_ID
        exchangePattern = RequestResponse

        restApi {
            id = "echo-api"
            apiSpecId = simpleRestResourceKey.toResourceIdentifier()
        }

        map {
            id = "echo-response-creator"
            mapSpec = """
                {
                    "message" : #input.payload.value
                }
            """.trimIndent()
        }

    }

}
