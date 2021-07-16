package flowexamples.common.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration


object WireMockTestManager {

    /**
     * Provide a callback with a unique wiremock server instance.
     *
     * @param config Optional Wiremock server config.
     *          By default the server is set up with a unique dynamic port which can be
     *          retrieved from the server instance. If a custom config is provided it is strongly encouraged that
     *          it also uses dynamic ports, since that provides isolation from other concurrent tests.
     * @param block the callback.
     *
     */
    fun withWireMock(
        config: WireMockConfiguration = WireMockConfiguration().dynamicPort(),
        block: (wireMockServer: WireMockServer) -> Unit
    ) {
        val server = WireMockServer(config)
        server.start()
        try {
            block(server)
        } finally {
            server.stop()
            server.resetAll()
        }
    }
}