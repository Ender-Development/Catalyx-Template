package util

import org.gradle.api.GradleException

enum class EnumProvider {
    CURSEFORGE("CF"),
    MODRINTH("MR"),
    MAVEN("MV"),
    ;

    val shortName: String

    constructor(shortName: String) {
        this.shortName = shortName
    }

    fun get(shortName: String) = values().firstOrNull { it.shortName == shortName } ?: throw GradleException("Unknown provider short name: $shortName")
}

@Suppress("unused")
enum class EnumConfiguration {
    /**
     * If you need this for internal implementation details of the mod, but none of it is visible via the public API
     * Available at runtime but not compile time for mods depending on this mod
     */
    IMPLEMENTATION("implementation"),

    /**
     * If the mod you're building doesn't need this dependency during runtime at all, e.g. for optional mods
     * Not available at all for mods depending on this mod, only visible at compile time for this mod
     */
    COMPILE_ONLY("compileOnly"),

    /**
     * If you don't need this at compile time, but want it to be present at runtime
     * Available at runtime for mods depending on this mod
     */
    RUNTIME_ONLY("runtimeOnly"),

    /**
     *  Mostly for java compiler plugins, if you know you need this, use it, otherwise don't worry
     */
    ANNOTATION_PROCESSOR("annotationProcessor"),

    /**
     *  If you want to embed this dependency into your mod jar
     *  NOT RECOMMENDED unless you absolutely have to
     */
    EMBED("embed"),

    /**
     *  Special configuration for patched Minecraft dependencies
     *  ONLY FOR INTERNAL USE. DO NOT USE THIS IN YOUR MODS
     */
    PATCHED_MINECRAFT("patchedMinecraft"),
    ;

    val configurationName: String

    constructor(configurationName: String) {
        this.configurationName = configurationName
    }

    override fun toString() = configurationName
}

typealias ModSource = String
typealias isTransitive = Boolean
typealias isChanging = Boolean
typealias ModDependency = Pair<ModSource, Pair<isTransitive, isChanging>>

abstract class AbstractDependency(val enabled: Boolean, private val transitive: Boolean?, private val changing: Boolean?) {
    abstract override fun toString(): String

    /**
     * Indicates whether the dependency is transitive.
     * Default is true if not explicitly set to false.
     */
    fun transitive(): Boolean = transitive != false

    /**
     * Indicates whether the dependency is changing.
     * Default is false if not explicitly set to true.
     */
    fun changing(): Boolean = changing == true

    fun modDependency(): ModDependency = Pair(toString(), Pair(transitive(), changing()))
}

class Maven(val group: String, val artifact: String, val version: String, enabled: Boolean, transitive: Boolean?, changing: Boolean?) : AbstractDependency(enabled, transitive, changing) {
    override fun toString() = "$group:$artifact:$version"
}

class Modrinth(val projectId: String, val fileId: String, enabled: Boolean, transitive: Boolean?, changing: Boolean?) : AbstractDependency(enabled, transitive, changing) {
    override fun toString() = "maven.modrinth:$projectId:$fileId"
}

class Curseforge(val projectName: String, val projectId: String, val fileId: String, enabled: Boolean, transitive: Boolean?, changing: Boolean?) : AbstractDependency(enabled, transitive, changing) {
    override fun toString() = "curse.maven:$projectName-$projectId:$fileId"
}
