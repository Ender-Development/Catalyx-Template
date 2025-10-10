import com.gtnewhorizons.retrofuturagradle.mcp.InjectTagsTask
import org.jetbrains.changelog.Changelog
import org.jetbrains.gradle.ext.Gradle

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
    id("com.matthewprenger.cursegradle") version "1.4.0" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
    id("com.diffplug.gradle.spotless") version "8.0.0" apply false
    id("org.jetbrains.changelog") version "2.2.1"
    id("at.stnwtr.gradle-secrets-plugin") version "1.0.1"
}

apply(from = "gradle/scripts/helpers.gradle.kts")

group = "org.ender_development"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {}