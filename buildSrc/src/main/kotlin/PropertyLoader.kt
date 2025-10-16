import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.Properties
import kotlin.jvm.java

object PropertyLoader {
    private val extraProperties = mutableMapOf<String, Map<String, String>>()

    internal fun loadProperties(
        filePath: String,
        external: Boolean = false
    ) {
        if (extraProperties.containsKey(filePath)) return
        val properties = getProperties(filePath, external)
        val map = properties.entries.associate { it.key.toString() to it.value.toString() }
        PluginLogger.log("Loaded ${map.size} properties from ${if (external) "external" else "internal"} '$filePath'")
        extraProperties[filePath] = map
    }

    internal fun getProperties(
        filePath: String,
        external: Boolean
    ): Properties =
        when {
            external -> loadExternal(filePath)
            else -> loadInternal(filePath)
        }

    private fun loadInternal(filePath: String): Properties {
        val properties = Properties()
        val inputStream: InputStream? = this::class.java.classLoader.getResourceAsStream(filePath)
        inputStream?.let { properties.load(it) }
            ?: throw FileNotFoundException("Properties file '$filePath' not found in resources.")
        return properties
    }

    private fun loadExternal(filePath: String): Properties {
        val properties = Properties()
        val file = File(filePath)
        if (file.exists()) {
            properties.load(file.inputStream())
        } else {
            PluginLogger.log("Properties file '$filePath' not found.")
        }
        return properties
    }

    internal fun getProperty(
        filePath: String,
        key: String
    ): String? = extraProperties[filePath]?.get(key)

    internal fun getAllProperties(): Map<String, String> = extraProperties.values.flatMap { it.entries }.associate { it.toPair() }
}
