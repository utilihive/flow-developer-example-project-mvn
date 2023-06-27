package flowexamples.e06

import flowexamples.e06.E06SoapFlow.echoNamespace

object E06ResponseTransformationMapping {

    /*
        Converting the NumberToWordsResponse to the SayHiResponse expected by the echo service exposed by our flow.

        Namespaces:
        - NumberConversion:   In soapRequest.responseNamespacePrefixMapping we have defined the n prefix for the
                              number conversion namespace. We must therefore access the NumberToWordsResponse response
                              elements with the "n_" namespace prefix.
        - EchoService:        We are binding the echo namespace to the default namespace. We can therefore define
                              the SayHiResponse response elements without namespace prefix.
    */
    val numberConversionResponseToEchoResponseMapping = """
        {
            "_xmlns" : {
                "_default" : "$echoNamespace"
            },
            "SayHiResponse" : {
                "HiResponse" : {
                    "_text" : #input.payload.n_NumberToWordsResponse.n_NumberToWordsResult._text
                }
            }
        }
    """.trimIndent()
}