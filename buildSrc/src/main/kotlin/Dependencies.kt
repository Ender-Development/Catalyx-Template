import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies

/**
 * Adds a dependency to the specified configuration with an option to set its transitivity.
 *
 * @param configuration The configuration to which the dependency should be added (e.g., "implementation", "compileOnly").
 * @param notation The dependency notation (e.g., "group:name:version").
 * @param transitive Whether the dependency should be transitive. Default is true.
 * @receiver The DependencyHandler to which the dependency is added.
 */
fun DependencyHandler.dep(configuration: String, notation: Any, transitive: Boolean = true) {
    PluginLogger.log("Adding dependency '$notation' to configuration '$configuration' (transitive=$transitive)")
    val dep = add(configuration, notation)
    (dep as? ModuleDependency)?.isTransitive = transitive
}

fun Project.loadDefaultDependencies() {
    dependencies {
        /**
         * Adds the dependency as an implementation dependency if the specified property is true,
         * otherwise adds it as a compileOnly dependency.
         *
         * @param run The name of the property to check.
         * @param transitive Whether the dependency should be transitive. Default is true.
         * @receiver The dependency notation to add.
         */
        fun String.dependency(run: String, transitive: Boolean = true) {
            val presentAtRuntime = propertyBoolean(run)
            if (presentAtRuntime) {
                dep("implementation", this, transitive)
            } else {
                dep("compileOnly", this, transitive)
            }
        }

        fun String.requiresMixins() {
            if (propertyBoolean("use_mixinbooter")) dep("runtimeOnly", this)
        }

        dep("compileOnly", "org.jetbrains:annotations:${propertyString("jetbrains_annotations_version")}")
        dep("annotationProcessor", "org.jetbrains:annotations:${propertyString("jetbrains_annotations_version")}")

        dep("patchedMinecraft", "net.minecraft:launchwrapper:1.17.2", false)

        // Include StripLatestForgeRequirements by default for the dev env, saves everyone a hassle
        "com.cleanroommc:strip-latest-forge-requirements:1.0".requiresMixins()
        // Include OSXNarratorBlocker by default for the dev env, for M1+ Macs
        "com.cleanroommc:osxnarratorblocker:1.0".requiresMixins()

        // Required dependencies
        "io.github.chaosunity.forgelin:Forgelin-Continuous:${propertyString("forgelin_continuous_version")}".dependency(
            "use_forgelincontinuous",
            false,
        )
        "com.cleanroommc:configanytime:${propertyString("configanytime_version")}".dependency("use_configanytime")
        "com.cleanroommc:assetmover:${propertyString("assetmover_version")}".dependency("use_assetmover")
        "com.cleanroommc:modularui:${propertyString("modularui_version")}".dependency("use_modularui", false)

        if (propertyBoolean("use_catalyx")) {
            dep("implementation", "org.ender_development:catalyx:${propertyString("catalyx_version")}", false)
        }

        // Optional dependencies
        "com.cleanroommc:groovyscript:${propertyString("groovyscript_version")}".dependency("use_groovyscript", false)
        "mezz:jei:${propertyString("hei_version")}".dependency("use_hei")

        "CraftTweaker2:CraftTweaker2-API:${propertyString("crafttweaker_version")}".dependency("use_crafttweaker")
        "CraftTweaker2:ZenScript:${propertyString("crafttweaker_version")}".dependency("use_crafttweaker")
        "CraftTweaker2:CraftTweaker2-MC1120-Main:1.12-${propertyString("crafttweaker_version")}".dependency("use_crafttweaker")
    }
}
