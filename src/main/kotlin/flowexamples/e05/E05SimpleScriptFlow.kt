package flowexamples.e05

import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceRevisionKey.Companion.newResourceRevisionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath
import flowexamples.e05.E05SimpleScript.simpleScript

object E05SimpleScriptFlow {
    val simpleScriptOpenApiDefinition = fromClasspath("/echo-open-api-v1.json")

    val simpleScriptResourceKey = newResourceRevisionKey {
        ownerId = OWNER_ID
        type = "OpenAPIv3"
        id = "simple-script-api"
        revision = "latest"
    }

    val simpleScriptSpec = flowConfig {
        id = "simple-script"
        name = id
        ownerId = OWNER_ID
        exchangePattern = RequestResponse

        restApi {
            id = "script-api"
            apiSpecId = simpleScriptResourceKey.toResourceIdentifier()
        }

        executeScript {
            id = "script-response"
            language = "JSON"
            script = simpleScript
        }
    }

}
