package flowexamples.e04

import com.greenbird.metercloud.integration.beanmapper.dsl.mappingConfig

object E04SimpleMapping {
    val simpleMapping = mappingConfig {
        id = "simple-mapping"
        displayName = id

        mapping {
            "value" to "message"
        }

    }
}