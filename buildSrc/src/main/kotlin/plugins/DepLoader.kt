package plugins

import dependency.AbstractDependency
import dependency.Curseforge
import dependency.EnumProvider
import dependency.Maven
import dependency.Modrinth
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.util.Properties

class DepLoader : Plugin<Project> {
    companion object {
        const val DEPENDENCIES = "dependencies.properties"

        private val dependencies = mutableListOf<AbstractDependency>()
        private val loadedProperties = mutableMapOf<String, MutableMap<String, String>>()

        private fun groupProperties(properties: Properties) {
            properties.forEach { (name, value) ->
                name as? String ?: return@forEach Logger.warn("Invalid dependency property name: $name")
                value as? String ?: return@forEach Logger.warn("Invalid dependency property value for $name: $value")
                val parts = name.split(".")
                if (parts.size != 3 || EnumProvider.values().any { it.shortName == parts[0] }.not()) {
                    Logger.warn("Invalid dependency property format: $name")
                    return@forEach
                }
                loadedProperties["${parts[0]}.${parts[1]}"] = (loadedProperties["${parts[0]}.${parts[1]}"] ?: mutableMapOf()).also {
                    it[parts[2]] = value
                }
            }
        }

        private fun populateDependencies() {
            loadedProperties.forEach { (key, value) ->
                val (providerKey, variableKey) = key.split(".")
                val provider = EnumProvider.values().first { it.shortName == providerKey }
                if (variableKey == "examplemod") return@forEach // Skip 'examplemod'
                val enabled = value["enabled"]?.toBoolean() ?: throw GradleException("Missing 'enabled' property for $key")
                val dependency = when {
                    provider == EnumProvider.CURSEFORGE && value.size == 4 -> Curseforge(
                        enabled,
                        value["projectName"]!!,
                        value["projectId"]!!,
                        value["fileId"]!!,
                    )

                    provider == EnumProvider.MODRINTH && value.size == 3 -> Modrinth(enabled, value["projectId"]!!, value["version"]!!)
                    provider == EnumProvider.MAVEN && value.size == 3 -> Maven(enabled, value["path"]!!, value["version"]!!)
                    else -> return@forEach Logger.warn("Invalid dependency configuration for $key with values $value")
                }
                dependencies.add(dependency)
            }
        }

        fun get(): Map<Boolean, String> {
            if (dependencies.isEmpty() && File(DEPENDENCIES).exists()) {
                val props = Loader.loadPropertyFromFile(DEPENDENCIES)
                if (props.isEmpty) return emptyMap()
                groupProperties(props)
                populateDependencies()
                if (dependencies.isEmpty().not()) Logger.warn("Dependencies have not been loaded until now, was the plugin not applied?")
            }
            return dependencies.associate { it.enabled to it.toString() }
        }
    }

    override fun apply(target: Project) {
        Logger.greet(this)
        if (File(DEPENDENCIES).exists().not()) {
            Logger.warn("No '$DEPENDENCIES' file found, skipping loading dependencies")
            return
        }
        groupProperties(Loader.loadPropertyFromFile(DEPENDENCIES))
        populateDependencies()
    }
}
