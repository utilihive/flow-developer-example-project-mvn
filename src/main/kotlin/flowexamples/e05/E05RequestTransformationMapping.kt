package flowexamples.e05

import flowexamples.e05.E05SoapFlow.numberConversionNamespace

object E05RequestTransformationMapping {

    /*
        Converting the echo SayHi request to a request for the NumberToWords operation of the public NumberConversion
        web service: https://www.dataaccess.com/webservicesserver/NumberConversion.wso?op=NumberToWords

        Namespaces:
        - EchoService:        In the soapApi.namespacePrefixMapping flow config we have defined the echo namespace as
                              the default. We can therefore access the SayHi request elements without namespace prefix.
        - NumberConversion:   We are binding the number conversion namespace to the "n" prefix, and are
                              therefore using the "n_" prefix to define the CapitalCity elements in the mapping.
    */
    val echoRequestToNumberConversionRequestMapping = """
        {
            "_xmlns" : {
                "n" : "$numberConversionNamespace"
            },
            "n_NumberToWords" : {
                "n_ubiNum" : {
                    "_text": #input.payload.SayHi.Hi._text
                }
            }
        }
    """.trimIndent()

}