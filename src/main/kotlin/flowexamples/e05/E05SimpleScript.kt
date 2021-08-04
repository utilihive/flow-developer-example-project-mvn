package flowexamples.e05

import org.intellij.lang.annotations.Language

object E05SimpleScript {
    @Language("ECMAScript 6")
    val simpleScript = """
        return {
                message : input.payload.value
            }
        """.trimIndent()
}