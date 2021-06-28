package com.example.flows

import com.example.flows.ExampleFlowData.OWNER_ID
import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceVersionKey

object ExampleTestFlow {
    val exampleOpenApiDoc = ExampleTestFlow::class.java.getResourceAsStream("/swagger-echo.json")!!
        .bufferedReader().use { it.readText() }

    val exampleResourceKey = ResourceVersionKey.newResourceVersionKey {
        ownerId = OWNER_ID
        type = "OpenAPIv3"
        id = "test-resource"
        version = "v1"
    }

    val exampleSpec = flowConfig {
        id = "exampleFLowId"
        name = id
        ownerId = OWNER_ID
        exchangePattern = FlowExchangePattern.RequestResponse

        restApi {
            id = "echo-api"
            apiSpecId = exampleResourceKey.toResourceIdentifier()
        }

        executeScript {
            id = "echo-response-creator"
            language = "JSON"
            script = """
                return {
                    message : input.payload.value
                }
                """.trimIndent()
        }

    }

}
