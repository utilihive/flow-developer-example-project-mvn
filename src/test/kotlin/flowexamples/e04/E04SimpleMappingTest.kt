package flowexamples.e04

import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.mapping.MappingTestContext.Companion.withMappingContext
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.TestInputBuilder.Companion.input
import flowexamples.e04.E04SimpleMapping.simpleMapping
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// example of unit testing a mapping
internal class E04SimpleMappingTest {

    @Test
    fun `GIVEN correct input THEN data mapped as expected`() {
        withMappingContext(simpleMapping) {
            val input = input(mapOf("value" to "testData"))
            val mappingResult = map(input)
            assertThat(mappingResult).isEqualTo(mapOf("message" to "testData"))
        }
    }

}