object PropertyLoader {

    private val extraProperties = mutableMapOf<String, Map<String, String>>()

    internal fun loadProperties(filePath: String, errorIfAbsent: Boolean = true) {
        if (extraProperties.containsKey(filePath)) return
        val inputStream = PropertyLoader::class.java.classLoader.getResourceAsStream(filePath)
            ?: if (errorIfAbsent) throw IllegalArgumentException("Property file '$filePath' not found in resources.") else return
        val properties = java.util.Properties()
        properties.load(inputStream)
        val map = properties.entries.associate { it.key.toString() to it.value.toString() }
        extraProperties[filePath] = map
    }

    internal fun getAllProperties(): Map<String, String> {
        return extraProperties.values.flatMap { it.entries }.associate { it.toPair() }
    }

    internal fun getProperty(filePath: String, propertyName: String): String? {
        return extraProperties[filePath]?.get(propertyName)
    }
}