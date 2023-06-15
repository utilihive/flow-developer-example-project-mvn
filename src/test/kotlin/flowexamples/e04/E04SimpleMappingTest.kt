package flowexamples.e04

import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.map.MapTestContext.Companion.mapConfig
import com.greenbird.utilihive.integration.flowdeveloper.sdk.flow.map.MapTestContext.Companion.withMap
import com.greenbird.utilihive.integration.flowdeveloper.sdk.testing.TestInputBuilder.Companion.input
import flowexamples.e04.E04SimpleMapping.simpleMapping
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// example of unit testing a mapping
internal class E04SimpleMappingTest {

    @Test
    fun `GIVEN correct input THEN data mapped as expected`() {
        val mappingConfig = mapConfig {
            mapSpec = simpleMapping
        }

        withMap(mappingConfig) {
            val input = input(mapOf("value" to "testData"))
            val mappingResult = map(input)
            assertThat(mappingResult).isEqualTo(mapOf("message" to "testData"))
        }
    }

}