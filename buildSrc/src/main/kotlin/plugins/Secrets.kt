package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class Secrets : Plugin<Project> {
    companion object {
        const val PROPERTIES_FILE = "secrets.properties"
        const val EXAMPLE_FILE = "secrets.example.properties"

        /**
         * Loads properties from the specified properties file located in the resources' directory.
         *
         * @param name The name of the property to load from the secrets.properties file.
         * @throws GradleException if the property is not found and errorIfAbsent is true.
         */
        fun get(name: String) = Loader.getPropertyFromFile(PROPERTIES_FILE, name)?.toString()

        /**
         * Loads properties from the specified properties file located in the resources' directory.
         * If the property is not found, it attempts to load it from the system environment variables.
         *
         * @param name The name of the property to load from the secrets.properties file or environment variables.
         * @return The value of the property or environment variable, or null if not found.
         */
        fun getOrEnvironment(name: String) = get(name) ?: System.getenv(name)?.toString()
    }

    override fun apply(target: Project) {
        Logger.greet(this)
        if (!target.rootProject.file(PROPERTIES_FILE).exists() && target.rootProject.file(EXAMPLE_FILE).exists()) {
            Logger.warn("No '$PROPERTIES_FILE' file found in the project root. Please create one based on '$EXAMPLE_FILE'.")
            println("WARNING: No '$PROPERTIES_FILE' file found in the project root. Please create one based on '$EXAMPLE_FILE'.")
        }
        if (File(PROPERTIES_FILE).exists()) {
            Loader.loadPropertyFile(PROPERTIES_FILE)
        }
    }
}
