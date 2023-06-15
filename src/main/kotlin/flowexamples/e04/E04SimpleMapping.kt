package flowexamples.e04

object E04SimpleMapping {
    val simpleMapping = """
        {
            "message" : #input.payload.value
        }
    """.trimIndent()
}