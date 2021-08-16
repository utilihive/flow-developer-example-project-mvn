package flowexamples.e05

import org.intellij.lang.annotations.Language

object E05SimpleScript {
    @Language("ES6")
    val simpleScript = """
        return {
                message : input.payload.value
            }
        """.trimIndent()
}