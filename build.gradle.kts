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