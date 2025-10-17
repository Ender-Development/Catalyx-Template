package plugins

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import util.AbstractDependency
import util.Curseforge
import util.EnumProvider
import util.Maven
import util.ModDependency
import util.Modrinth
import java.io.File
import java.util.Properties

class DepLoader : Plugin<Project> {
    companion object {
        const val DEPENDENCIES = "dependencies.properties"

        private val dependencies = mutableListOf<AbstractDependency>()
        private val loadedProperties = mutableMapOf<String, MutableMap<String, String>>()

        private fun groupProperties(properties: Properties, silent: Boolean = false) {
            properties.forEach { (name, value) ->
                name as? String ?: return@forEach Logger.warn("Invalid dependency property name: $name")
                value as? String ?: return@forEach Logger.warn("Invalid dependency property value for $name: $value")
                val parts = name.split(".")
                if (parts.size != 3 || EnumProvider.values().any { it.shortName == parts[0] }.not()) {
                    if (!silent) Logger.warn("Invalid dependency property format: $name")
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
                val transitive = value["transitive"]?.toBoolean()
                val changing = value["changing"]?.toBoolean()
                val dependency = when {
                    provider == EnumProvider.CURSEFORGE && value.keys.containsAll(
                        listOf(
                            "projectName",
                            "projectId",
                            "fileId",
                        ),
                    ) -> Curseforge(
                        value["projectName"]!!,
                        value["projectId"]!!,
                        value["fileId"]!!,
                        enabled,
                        transitive,
                        changing,
                    )

                    provider == EnumProvider.MAVEN && value.keys.containsAll(listOf("group", "artifact", "version")) -> Maven(
                        value["group"]!!,
                        value["artifact"]!!,
                        value["version"]!!,
                        enabled,
                        transitive,
                        changing,
                    )

                    provider == EnumProvider.MODRINTH && value.keys.containsAll(listOf("projectId", "version")) -> Modrinth(
                        value["projectId"]!!,
                        value["version"]!!,
                        enabled,
                        transitive,
                        changing,
                    )

                    else -> return@forEach Logger.warn("Invalid dependency configuration for $key with values $value")
                }
                dependencies.add(dependency)
            }
        }

        fun get(): Map<Boolean, ModDependency> {
            if (dependencies.isEmpty() && File(DEPENDENCIES).exists()) {
                val props = Loader.loadPropertyFromFile(DEPENDENCIES)
                if (props.isEmpty) return emptyMap()
                groupProperties(props)
                populateDependencies()
                if (dependencies.isEmpty().not()) Logger.warn("Dependencies have not been loaded until now, was the plugin not applied?")
            }
            Logger.info("Found ${dependencies.size} external dependenc${if (dependencies.size != 1) "y" else "ies"}")
            return dependencies.associate { it.enabled to it.modDependency() }
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
