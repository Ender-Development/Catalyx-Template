import groovy.lang.GroovyShell
import java.util.*

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlinVersion}")
    }
}

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    kotlin("jvm") version libs.versions.kotlinVersion
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.9"
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.1"
    id("org.jetbrains.changelog") version "2.2.1"
    // Publishing
    id("com.matthewprenger.cursegradle") version "1.4.0" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
    // Formatters
    id("com.diffplug.gradle.spotless") version "8.0.0" apply false
}

loadProjectProperties()
loadAllProperties()

checkPropertyExists("root_package")
checkPropertyExists("mod_id")
propertyDefaultIfUnset("mod_name", propertyString("mod_id"))
checkPropertyExists("mod_version")
checkPropertyExists("minecraft_version")

propertyDefaultIfUnsetWithEnvVar("minecraft_username", "DEV_USERNAME", "Developer")

// Utilities
checkSubPropertiesExist("use_tags", "tag_class_name")
checkSubPropertiesExist("use_access_transformer", "access_transformer_locations")
checkSubPropertiesExist("is_coremod", "coremod_includes_mod", "coremod_plugin_class_name")

// Dependencies
checkSubPropertiesExist("use_assetmover", "assetmover_version")
checkSubPropertiesExist("use_catalyx", "catalyx_version")
checkSubPropertiesExist("use_configanytime", "configanytime_version")
checkSubPropertiesExist("use_forgelincontinuous", "forgelin_continuous_version")
checkSubPropertiesExist("use_mixinbooter", "mixin_booter_version", "mixin_refmap")
checkSubPropertiesExist("use_modularui", "modularui_version")

// Integrations
checkSubPropertiesExist("use_crafttweaker", "crafttweaker_version")
checkSubPropertiesExist("use_groovyscript", "groovyscript_version")
checkSubPropertiesExist("use_hei", "hei_version")
checkSubPropertiesExist("use_top", "top_version")

group = propertyString("root_package")
version = propertyString("mod_version")

base {
    archivesName = propertyString("mod_id")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        // Azul covers the most platforms for Java 8+ toolchains, crucially including MacOS arm64
        vendor.set(JvmVendorSpec.AZUL)
    }
    if (propertyBoolean("generate_sources_jar")) withSourcesJar()

    if (propertyBoolean("generate_javadocs_jar")) withJavadocJar()

}

kotlin {
    jvmToolchain(8)
}

minecraft {
    mcVersion = propertyString("minecraft_version")
    mcpMappingChannel = propertyString("mapping_channel")
    mcpMappingVersion = propertyString("mapping_version")

    username = propertyString("minecraft_username")

    useDependencyAccessTransformers = propertyBoolean("use_dependency_at_files")

    extraRunJvmArguments.add("-ea:$group")
    val args = mutableListOf<String>()
    if (propertyBoolean("use_mixinbooter")) {
        args.add("-Dmixin.hotSwap=true")
        args.add("-Dmixin.checks.interfaces=true")
        args.add("-Dmixin.debug.export=true")
    }
    if (propertyBoolean("is_coremod")) {
        args.add("-Dlegacy.debugClassLoading=true")
        args.add("-Dlegacy.debugClassLoadingFiner=true")
        args.add("-Dlegacy.debugClassLoadingSave=true")
    }
    extraRunJvmArguments.addAll(args)
    extraRunJvmArguments.addAll(propertyStringList("extra_jvm_args", delimiter = ";"))

    if (propertyBoolean("use_tags") && file("gradle/properties/tags.properties").exists()) {
        val props = Properties().apply { load(file("gradle/properties/tags.properties").inputStream()) }
        if (props.isNotEmpty()) {
            injectedTags.set(props.map { it.key.toString() to evaluate(it.value.toString()) }.toMap())
        }
    }
}

repositories {
    mavenCentral()
}
apply(from = "gradle/scripts/repositories.gradle.kts")

dependencies {
    /**
     * Adds the dependency as an implementation dependency if the specified property is true,
     * otherwise adds it as a compileOnly dependency.
     *
     * @param run The name of the property to check.
     * @receiver The dependency notation to add.
     */
    fun String.dependency(run: String, transitive: Boolean = true, useRFG: Boolean = false) {
        val presentAtRuntime = propertyBoolean(run)
        if (useRFG) {
            if (presentAtRuntime) implementation(rfg.deobf(this))
            else compileOnly(rfg.deobf(this))
            return
        }
        if (presentAtRuntime) implementation(this) { isTransitive = transitive }
        else compileOnly(this) { isTransitive = transitive }
    }

    compileOnlyApi("org.jetbrains:annotations:24.1.0")
    annotationProcessor("org.jetbrains:annotations:24.1.0")
    patchedMinecraft("net.minecraft:launchwrapper:1.17.2") {
        isTransitive = false
    }

    // Include StripLatestForgeRequirements by default for the dev env, saves everyone a hassle
    runtimeOnly("com.cleanroommc:strip-latest-forge-requirements:1.0")
    // Include OSXNarratorBlocker by default for the dev env, for M1+ Macs
    runtimeOnly("com.cleanroommc:osxnarratorblocker:1.0")

    // Mixins
    if (propertyBoolean("use_mixinbooter") || propertyBoolean("use_modularui")) {
        val mixin = modUtils.enableMixins(
            "zone.rong:mixinbooter:${propertyString("mixin_booter_version")}", propertyString("mixin_refmap")
        ).toString()
        api(mixin) {
            isTransitive = false
        }
        annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
        annotationProcessor("com.google.guava:guava:32.1.2-jre")
        annotationProcessor("com.google.code.gson:gson:2.8.9")
        annotationProcessor(mixin) {
            isTransitive = false
        }
    }

    // Required dependencies
    "io.github.chaosunity.forgelin:Forgelin-Continuous:${propertyString("forgelin_continuous_version")}".dependency(
        "use_forgelincontinuous", false
    )
    "com.cleanroommc:configanytime:${propertyString("configanytime_version")}".dependency("use_configanytime")
    "com.cleanroommc:assetmover:${propertyString("assetmover_version")}".dependency("use_assetmover")
    "com.cleanroommc:modularui:${propertyString("modularui_version")}".dependency("use_modularui", false)

    if (propertyBoolean("use_catalyx")) {
        implementation("org.ender_development:catalyx:${propertyString("catalyx_version")}")
    }

    // Optional dependencies
    "com.cleanroommc:groovyscript:${propertyString("groovyscript_version")}".dependency("use_groovyscript", false)
    "mezz:jei:${propertyString("hei_version")}".dependency("use_hei")
    "curse.maven:theonesmeagle-977883:${propertyString("top_version")}".dependency("use_top", useRFG = true)

    "CraftTweaker2:CraftTweaker2-API:${propertyString("crafttweaker_version")}".dependency("use_crafttweaker")
    "CraftTweaker2:ZenScript:${propertyString("crafttweaker_version")}".dependency("use_crafttweaker")
    "CraftTweaker2:CraftTweaker2-MC1120-Main:1.12-${propertyString("crafttweaker_version")}".dependency("use_crafttweaker")
}

// Manage Access Transformers
if (propertyBoolean("use_access_transformer")) {
    propertyStringList("access_transformer_locations").forEach {
        val atFile = file("$projectDir/src/main/resources/$it")
        if (atFile.exists()) {
            tasks.deobfuscateMergedJarToSrg.get().accessTransformerFiles.from(atFile)
            tasks.srgifyBinpatchedJar.get().accessTransformerFiles.from(atFile)
        } else {
            throw GradleException("Access Transformer file '$it' does not exist!")
        }
    }
}

// Utility functions

/**
 * Loads properties from specified property files and adds them to the project's extra properties.
 * The property files are expected to be located in the 'gradle/properties' directory.
 * If a property file does not exist, a message is printed to the console.
 */
fun loadProjectProperties() {
    val propertyFiles = listOf(
        "build.properties",
        "dependencies.properties",
        "integration.properties",
        "publishing.properties",
        "utilities.properties"
    )
    propertyFiles.forEach { fileName ->
        val configFile = file("gradle/properties/$fileName")
        if (configFile.exists()) {
            configFile.bufferedReader().use { reader ->
                val properties = Properties()
                properties.load(reader)
                properties.forEach { (key, value) ->
                    project.extensions.extraProperties.set(key.toString(), value)
                }
                project.logger.info("Loaded properties from $fileName: $properties")
            }
        } else {
            project.logger.warn("Failed to read from $fileName, as it did not exist!")
        }
    }
}

/**
 * Checks if a property with the given name exists in the project's properties.
 * If the property does not exist, a GradleException is thrown.
 *
 * @param propertyName The name of the property to check.
 * @throws GradleException if the property does not exist.
 */
fun checkPropertyExists(propertyName: String) {
    if (!project.hasProperty(propertyName)) {
        throw GradleException("Property '$propertyName' is missing in your properties files.")
    }
}

/**
 * Checks if a property with the given name exists in the project's properties.
 * If the property exists and its value is true, it checks for the existence of all specified sub-properties.
 * If any property does not exist, a GradleException is thrown.
 *
 * @param propertyName The name of the main property to check.
 * @param subProperties The names of the sub-properties to check if the main property is true.
 * @throws GradleException if any property does not exist.
 */
fun checkSubPropertiesExist(propertyName: String, vararg subProperties: String) {
    checkPropertyExists(propertyName)
    if (propertyBoolean(propertyName)) subProperties.forEach { checkPropertyExists(it) }
}

/**
 * Retrieves the value of a property, interpolating any placeholders in the format `${propertyName}`.
 * If the property does not exist, it returns null.
 *
 * @param propertyName The name of the property to retrieve.
 * @return The value of the property with placeholders interpolated.
 * @throws GradleException if the property does not exist.
 */
private fun property(propertyName: String): Any? {
    checkPropertyExists(propertyName)
    val value = project.findProperty(propertyName)
    return if (value is String) evaluate(value) else value
}

/**
 * Evaluates expressions in the format `${{expression}}` within the given string.
 * Expressions are evaluated using the Kotlin scripting engine.
 * Expressions can contain property placeholders in the format `${propertyName}` which will be replaced before evaluation.
 *
 * @param value The string containing expressions to evaluate.
 * @return The string with all expressions evaluated and replaced by their results.
 * @throws GradleException if an expression fails to evaluate.
 */
fun evaluate(value: String): String {
    if (value.startsWith("\${{") && value.endsWith("}}")) {
        val expression = placeHolder(value.substring(3, value.length - 2).trim())
        return try {
            val eval = GroovyShell().evaluate(expression).toString()
            project.logger.info("Evaluated expression '$expression' to '$eval'")
            eval
        } catch (e: Exception) {
            throw GradleException("Failed to evaluate expression '$expression' in property evaluation.", e)
        }
    }
    return placeHolder(value)
}

/**
 * Replaces placeholders in the format `${propertyName}` within the given string with their corresponding property values.
 *
 * @param value The string containing placeholders to replace.
 * @return The string with all placeholders replaced by their property values.
 */
private fun placeHolder(value: String): String {
    var result = value
    val template = "\\$\\{([^}]+)}".toRegex()
    template.findAll(value).forEach { matchResult ->
        val placeholder = matchResult.value
        val key = matchResult.groupValues[1]
        val replacement = propertyString(key)
        result = result.replace(placeholder, replacement)
    }
    return result
}

/**
 * Retrieves the value of a property as a String.
 * If the property does not exist, a GradleException is thrown.
 *
 * @param propertyName The name of the property to retrieve.
 * @return The value of the property as a String.
 * @throws GradleException if the property does not exist.
 */
fun propertyString(propertyName: String): String = property(propertyName).toString()

/**
 * Retrieves the value of a property as a List of Strings, split by the specified delimiter.
 * If the property does not exist, a GradleException is thrown.
 *
 * @param propertyName The name of the property to retrieve.
 * @param delimiter The delimiter to use for splitting the property value. Default is a space (" ").
 * @return The value of the property as a List of Strings.
 * @throws GradleException if the property does not exist.
 */
fun propertyStringList(propertyName: String, delimiter: String = " "): List<String> =
    propertyString(propertyName).split(delimiter).filter { it.isNotEmpty() }

/**
 * Retrieves the value of a property as a Boolean.
 * If the property does not exist, a GradleException is thrown.
 *
 * @param propertyName The name of the property to retrieve.
 * @return The value of the property as a Boolean.
 * @throws GradleException if the property does not exist.
 */
fun propertyBoolean(propertyName: String): Boolean = propertyString(propertyName).toBoolean()

/**
 * Sets a default value for a property if it is not already set or is empty.
 *
 * @param propertyName The name of the property to check and potentially set.
 * @param defaultValue The default value to set if the property is not set or is empty.
 */
fun propertyDefaultIfUnset(propertyName: String, defaultValue: Any?) {
    if (!project.hasProperty(propertyName) || project.property(propertyName).toString().isEmpty()) {
        project.extensions.extraProperties.set(propertyName, defaultValue)
    }
}

/**
 * Sets a property to the value of an environment variable if it exists; otherwise, sets it to a default value.
 * It also checks [secrets.properties](https://github.com/stnwtr/gradle-secrets-plugin) for the environment variable.
 *
 * @param propertyName The name of the property to set.
 * @param envVarName The name of the environment variable to check.
 * @param defaultValue The default value to set if the environment variable is not set.
 */
fun propertyDefaultIfUnsetWithEnvVar(propertyName: String, envVarName: String, defaultValue: Any?) {
    // Searches in the 'secrets.properties' first. If not found in the file, it checks the environment variables.
    // If neither is found it will return null.
//    val envVarValue = secrets.getOrEnv(envVarName)
//    envVarValue?.let {
//        project.extensions.extraProperties.set(propertyName, it)
//    } ?: propertyDefaultIfUnset(propertyName, defaultValue)
    propertyDefaultIfUnset(propertyName, defaultValue)
}

