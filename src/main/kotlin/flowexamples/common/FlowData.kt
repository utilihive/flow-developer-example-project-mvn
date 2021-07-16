package flowexamples.common

object FlowData {
    const val OWNER_ID: String = "exampleOwner"

    fun fromClasspath(resourcePath: String): String {
        require(resourcePath.isNotBlank()) { "Resource path was blank." }
        val path = if (!resourcePath.startsWith("/")) "/$resourcePath" else resourcePath
        val stream = FlowData::class.java.getResourceAsStream(path)
        requireNotNull(stream) { "$resourcePath not found." }
        return stream.bufferedReader().use { it.readText() }
    }
}