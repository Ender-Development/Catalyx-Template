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
         */
        fun get(name: String) = Loader.getPropertyFromFile(PROPERTIES_FILE, name)

        /**
         * Loads properties from the specified properties file located in the resources' directory.
         * If the property is not found, it attempts to load it from the system environment variables.
         *
         * @param name The name of the property to load from the secrets.properties file or environment variables.
         * @return The value of the property or environment variable, or null if not found.
         */
        fun getOrEnvironment(name: String): String? = get(name) ?: System.getenv(name)
    }

    override fun apply(target: Project) {
	    Logger.greet(this)

	    val rootSecrets = target.rootProject.file(PROPERTIES_FILE)
	    val userSecrets = File(
		    System.getProperty("user.home"),
		    ".gradle/$PROPERTIES_FILE"
	    )
	    // Searches for PROPERTIES_FILE in ~/.gradle/
	    //     -> No need to store secrets inside the repo tree anymore BECAUSE IT IS FCKN DANGEROUS!
	    //        Even with .gitignore!
	    //        Thx, Klebi <3


	    val secretsFile = when {
		    rootSecrets.exists() -> {
			    if (!getOrEnvironment("DISMISS_SECRET_FILE_WARNING").toBoolean()) {
				    Logger.warn("Don't store secrets inside the repo tree BECAUSE IT IS FCKN DANGEROUS!")
			    }
				rootSecrets
		    }
		    userSecrets.exists() -> userSecrets
		    else -> null
	    }

	    if (secretsFile == null) {
		    if (target.rootProject.file(EXAMPLE_FILE).exists()) {
			    Logger.warn("No '$PROPERTIES_FILE' found. Please create one in project root or even better in ~/.gradle based on '$EXAMPLE_FILE'.")

				// Needed?
				//println("WARNING: No '$PROPERTIES_FILE' found in project root or ~/.gradle. Please create one based on '$EXAMPLE_FILE'.")
		    }
		    return
	    }

	    Logger.info("Loading secrets from: ${secretsFile.absolutePath}")
	    Loader.loadPropertyFile(secretsFile.absolutePath)
    }
}
