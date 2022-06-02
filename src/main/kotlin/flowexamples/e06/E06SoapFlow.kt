package flowexamples.e06

import com.greenbird.metercloud.integration.beanmapper.dsl.toDtoJson
import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceRevisionKey.Companion.newResourceRevisionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath
import flowexamples.e06.E06RequestTransformationMapping.echoRequestToNumberConversionRequestMapping
import flowexamples.e06.E06ResponseTransformationScript.numberConversionResponseToEchoResponseScript
import java.net.URL

object E06SoapFlow {
    const val echoNamespace = "http://www.bccs.uib.no/EchoService.wsdl"
    const val numberConversionNamespace = "http://www.dataaccess.com/webservicesserver/"

    // We're mapping a simple echo API deployed on the Utilihive server
    val soapFrontendDefinition = fromClasspath("/EchoService.wsdl")

    // ...to a public number conversion service
    val soapBackendDefinition = fromClasspath("/NumberConversionService.wsdl")

    val soapFrontendResourceKey = newResourceRevisionKey {
        ownerId = OWNER_ID
        type = "WSDLv1"
        id = "soap-echo-num-conversion"
        revision = "latest"
    }

    val soapBackendResourceKey = newResourceRevisionKey {
        ownerId = OWNER_ID
        type = "WSDLv1"
        id = "soap-num-conversion"
        revision = "latest"
    }

    /*
        Exposing a simple EchoService soap API and integrating that with the public NumberConversion web service.

        We are using bean-mapping and script conversions with our XML compliant JSON format to convert messages between
        the two service domains.
    */
    val soapFrontendFlowSpec = flowConfig {
        id = "soap"
        name = id
        ownerId = OWNER_ID
        exchangePattern = RequestResponse

        soapApi {
            id = "soap-echo-num-conversion-api"
            wsdlSpecId = soapFrontendResourceKey.toResourceIdentifier()
            serviceName = "EchoService"
            portName = "EchoService"
            namespacePrefixMapping = """
                _default = $echoNamespace
            """.trimIndent()
        }

        beanTransformer {
            id = "echo-req-to-num-conversion"
            transformerSpec = echoRequestToNumberConversionRequestMapping.toDtoJson()
        }

        soapRequest {
            id = "soap-num-conversion-request"
            address = URL("https://www.dataaccess.com/webservicesserver/NumberConversion.wso")
            wsdlSpecId = soapBackendResourceKey.toResourceIdentifier()
            serviceName = "NumberConversion"
            portName = "NumberConversionSoap"
            responseNamespacePrefixMapping = """
                n=$numberConversionNamespace
            """.trimIndent()
        }

        executeScript {
            id = "no-to-words-resp-to-echo-resp"
            language = "JSON"
            script = numberConversionResponseToEchoResponseScript
        }

    }

}
