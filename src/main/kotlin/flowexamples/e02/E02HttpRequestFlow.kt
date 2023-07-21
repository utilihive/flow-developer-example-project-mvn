package flowexamples.e02

import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.HttpMethod.POST
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceRevisionKey.Companion.newResourceRevisionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath
import java.net.URL

object E02HttpRequestFlow {
    const val BACKEND_AUTHENTICATION_KEY = "backendAuth"
    val httpRequestOpenApiDefinition = fromClasspath("/echo-open-api-v1.json")

    val httpRequestResourceKey = newResourceRevisionKey {
        ownerId = OWNER_ID
        type = "OpenAPIv3"
        id = "http-request-api"
        revision = "latest"
    }

    val httpRequestSpec = flowConfig {
        id = "http-request"
        description = "HTTP Request Flow"
        ownerId = OWNER_ID
        exchangePattern = RequestResponse

        restApi {
            id = "echo-api"
            apiSpecId = httpRequestResourceKey.toResourceIdentifier()
        }

        restRequest {
            id = "rest-request"
            defaultMethod = POST

            // Actual backend addresses are typically specified during the deployment process
            // for each deployment environment. So here we just add a placeholder value.
            address = URL("https://OVERRIDE_ME/echo")

            // In live deployment the backend credentials will be looked up externally.
            // For the tests they are defined on the test config and deployed to the test server by the sdk.
            authenticationConfigKey = BACKEND_AUTHENTICATION_KEY
        }

    }

}
