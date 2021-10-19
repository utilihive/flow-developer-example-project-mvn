package flowexamples.e06

import com.greenbird.metercloud.integration.flow.spec.FlowExchangePattern.RequestResponse
import com.greenbird.metercloud.integration.flow.spec.dsl.flowConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.resources.ResourceVersionKey.Companion.newResourceVersionKey
import flowexamples.common.FlowData.OWNER_ID
import flowexamples.common.FlowData.fromClasspath
import flowexamples.e06.E06TransformationScripts.capitalCityResponseToEchoResponseScript
import flowexamples.e06.E06TransformationScripts.echoRequestToCapitalCityRequestScript
import java.net.URL

object E06SoapFlows {
    const val echoNamespace = "http://www.bccs.uib.no/EchoService.wsdl"
    const val countryInfoNamespace = "http://www.oorsprong.org/websamples.countryinfo"

    // We're mapping a simple echo API deployed on the Utilihive server
    val soapFrontendDefinition = fromClasspath("/EchoService.wsdl")

    // ...to a public country info service
    val soapBackendDefinition = fromClasspath("/CountryInfoService.wsdl")

    val soapFrontendResourceKey = newResourceVersionKey {
        ownerId = OWNER_ID
        type = "WSDLv1"
        id = "soap-echo-capital-city"
        version = "v1"
    }

    val soapBackendResourceKey = newResourceVersionKey {
        ownerId = OWNER_ID
        type = "WSDLv1"
        id = "soap-country-info"
        version = "v1"
    }

    /*
        Exposing a simple EchoService soap API and integrating that with the public CountryInfoService web service.
        We are using script conversions with our XML compliant JSON format to convert messages between the
        two service domains.
    */
    val soapFrontendFlowSpec = flowConfig {
        id = "soap"
        name = id
        ownerId = OWNER_ID
        exchangePattern = RequestResponse

        soapApi {
            id = "soap-echo-capital-city-api"
            wsdlSpecId = soapFrontendResourceKey.toResourceIdentifier()
            serviceName = "EchoService"
            portName = "EchoService"
            namespacePrefixMapping = """
                _default = $echoNamespace
            """.trimIndent()
        }

        executeScript {
            id = "echo-req-to-capital-req"
            language = "JSON"
            script = echoRequestToCapitalCityRequestScript
        }

        soapRequest {
            id = "soap-country-info-request"
            address = URL("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso")
            wsdlSpecId = soapBackendResourceKey.toResourceIdentifier()
            serviceName = "CountryInfoService"
            portName = "CountryInfoServiceSoap"
        }

        executeScript {
            id = "capital-resp-to-echo-resp"
            language = "JSON"
            script = capitalCityResponseToEchoResponseScript
        }

    }

}
