package flowexamples.e05

import com.greenbird.utilihive.integration.flowdeveloper.sdk.client.ws.WebServiceClientConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.Resource
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.FlowTestManager.Companion.flowTest
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.addFlowTestConfig
import com.greenbird.utilihive.integration.test.concurrent.core.ConcurrentTestContext
import com.greenbird.utilihive.integration.test.concurrent.core.junit5.ConcurrentTestBase
import flowexamples.e05.E05SoapFlow.echoNamespace
import flowexamples.e05.E05SoapFlow.soapBackendDefinition
import flowexamples.e05.E05SoapFlow.soapBackendResourceKey
import flowexamples.e05.E05SoapFlow.soapFrontendDefinition
import flowexamples.e05.E05SoapFlow.soapFrontendFlowSpec
import flowexamples.e05.E05SoapFlow.soapFrontendResourceKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class E05SoapFlowTest : ConcurrentTestBase() {

    @Test
    fun `E05 GIVEN live SOAP backend WHEN sending a number THEN the number converted to words is returned`(
        ctx: ConcurrentTestContext
    ) {
        val number = "10428"
        val numberAsWord = "ten thousand four hundred and twenty eight"

        val frontendSoapApiResource = Resource(key = soapFrontendResourceKey, content = soapFrontendDefinition)
        val backendSoapApiResource = Resource(key = soapBackendResourceKey, content = soapBackendDefinition)

        ctx.addFlowTestConfig {
            resource(frontendSoapApiResource)
            resource(backendSoapApiResource)
            flow(soapFrontendFlowSpec)
        }

        val clientConfig = WebServiceClientConfig.newWebServiceClientConfig {
            flowSpec = soapFrontendFlowSpec
            serviceName = "EchoService"
            portName = "EchoService"
            wsdlDoc = soapFrontendDefinition
        }


        flowTest(ctx) {
            val nsMap = mapOf("_default" to echoNamespace)
            withWebServiceFlowClient(clientConfig) {
                val xmlJson = mapOf(
                    "_xmlns" to nsMap,
                    "SayHi" to mapOf("Hi" to mapOf("_text" to number))
                )

                val response = request(xmlJson, nsMap)
                assertThat(response).isEqualTo(
                    mapOf(
                        "_declaration" to mapOf("standalone" to "no", "version" to "1.0"),
                        "_xmlns" to nsMap,
                        // the service returns an extra space at the end of the last word
                        "SayHiResponse" to mapOf("HiResponse" to mapOf("_text" to "$numberAsWord "))
                    )
                )

            }
        }
    }

}
