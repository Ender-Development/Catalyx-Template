import org.gradle.api.GradleException

object SecretsManager {
    const val PROPERTIES_FILE = "secrets.properties"
    const val EXAMPLE_FILE = "secrets.example.properties"

    /**
     * Loads properties from the specified properties file located in the resources' directory.
     *
     * @param name The name of the property to load from the secrets.properties file.
     * @throws GradleException if the property is not found and errorIfAbsent is true.
     */
    fun get(name: String) = PropertyLoader.getProperty(PROPERTIES_FILE, name)?.toString()

    /**
     * Loads properties from the specified properties file located in the resources' directory.
     * If the property is not found, it attempts to load it from the system environment variables.
     *
     * @param name The name of the property to load from the secrets.properties file or environment variables.
     * @return The value of the property or environment variable, or null if not found.
     */
    fun getOrEnvironment(name: String) = get(name) ?: System.getenv(name)?.toString()
}