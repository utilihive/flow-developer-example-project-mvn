package flowexamples.deployment

import com.greenbird.metercloud.integration.flow.spec.dsl.FlowSpecConfig
import com.greenbird.utilihive.integration.deployment.flow.FlowDeploy
import flowexamples.e01.E01SimpleRestFlow.simpleRestSpec

/**
 * Example of a class providing a flow factory function for the Deployer.
 */
object FlowDeployment {

    @FlowDeploy
    fun simpleRest(): FlowSpecConfig = simpleRestSpec.copy(
        ownerId = "sdktraining" // reuse the E01 flow, but override the owner id
    )

}