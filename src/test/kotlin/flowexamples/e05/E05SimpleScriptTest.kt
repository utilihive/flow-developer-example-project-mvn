package flowexamples.e05

import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.scripting.JsonScript.Companion.jsonScript
import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.scripting.ScriptingTestContext.Companion.withJsonScript
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.TestInputBuilder.Companion.input
import flowexamples.e05.E05SimpleScript.simpleScript
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// example of unit testing a script
internal class E05SimpleScriptTest {

    @Test
    fun `GIVEN correct input THEN data returned as expected`() {
        val script = jsonScript(simpleScript)
        withJsonScript(script) {
            val input = input(mapOf("value" to "testData"))
            val mappingResult: Map<String, Any> = execute(input)
            assertThat(mappingResult).isEqualTo(mapOf("message" to "testData"))
        }
    }

}