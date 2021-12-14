package flowexamples.e06

import com.greenbird.metercloud.integration.beanmapper.dsl.mappingConfig
import flowexamples.e06.E06SoapFlow.numberConversionNamespace

object E06RequestTransformationMapping {

    /*
        Converting the echo SayHi request to a request for the NumberToWords operation of the public NumberConversion
        web service: https://www.dataaccess.com/webservicesserver/NumberConversion.wso?op=NumberToWords

        Namespaces:
        - EchoService:        In the soapApi.namespacePrefixMapping flow config we have defined the echo namespace as
                              the default. We can therefore access the SayHi request elements without namespace prefix.
        - NumberConversion:   We are binding the number conversion namespace to the "n" prefix, and are
                              therefore using the "n_" prefix to define the CapitalCity elements in the mapping.
    */
    val echoRequestToNumberConversionRequestMapping = mappingConfig {
        id = "echo-request-to-number-conversion-request-mapping"
        displayName = id

        mapping {
            "_xmlns.n" setTo numberConversionNamespace
            "SayHi.Hi._text" to "n_NumberToWords.n_ubiNum._text"
        }
    }

}