package flowexamples.e05

import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceRevisionKey.Companion.newResourceRevisionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath
import flowexamples.e05.E05RequestTransformationMapping.echoRequestToNumberConversionRequestMapping
import flowexamples.e05.E05ResponseTransformationMapping.numberConversionResponseToEchoResponseMapping
import java.net.URL

object E05SoapFlow {
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

        We are using data mapping with our XML compliant JSON format to convert messages between the two service domains.
    */
    val soapFrontendFlowSpec = flowConfig {
        id = "soap"
        description = "SOAP Flow"
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

        map {
            id = "echo-req-to-num-conversion"
            mapSpec = echoRequestToNumberConversionRequestMapping
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

        map {
            id = "no-to-words-resp-to-echo-resp"
            mapSpec = numberConversionResponseToEchoResponseMapping
        }

    }

}
