package flowexamples.e06

import com.greenbird.metercloud.integration.beanmapper.dsl.mappingConfig
import flowexamples.e06.E06SoapFlow.countryInfoNamespace

object E06RequestTransformationMapping {

    /*
        Converting the echo SayHi request to a request for the CapitalCity operation of the public CountryInfoService
        web service: http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?op=CapitalCity

        Namespaces:
        - EchoService:        In the soapApi.namespacePrefixMapping flow config we have defined the echo namespace as
                              the default. We can therefore access the SayHi request elements without namespace prefix.
        - CountryInfoService: We are binding the country info namespace to the "c" prefix, and are
                              therefore using the "c_" prefix to define the CapitalCity elements in the mapping.
    */
    val echoRequestToCapitalCityRequestMapping = mappingConfig {
        id = "echo-request-to-capital-city-request-mapping"
        displayName = id

        mapping {
            "_xmlns.c" setTo countryInfoNamespace
            "SayHi.Hi._text" to "c_CapitalCity.c_sCountryISOCode._text"
        }
    }

}