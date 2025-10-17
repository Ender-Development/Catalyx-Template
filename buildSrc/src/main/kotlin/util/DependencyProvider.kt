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

    fun get(shortName: String) =
        values().firstOrNull { it.shortName == shortName } ?: throw GradleException("Unknown provider short name: $shortName")
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

class Maven(val group: String, val artifact: String, val version: String, enabled: Boolean, transitive: Boolean?, changing: Boolean?) :
    AbstractDependency(enabled, transitive, changing) {
    override fun toString() = "$group:$artifact:$version"
}

class Modrinth(val projectId: String, val fileId: String, enabled: Boolean, transitive: Boolean?, changing: Boolean?) :
    AbstractDependency(enabled, transitive, changing) {
    override fun toString() = "maven.modrinth:$projectId:$fileId"
}

class Curseforge(
    val projectName: String,
    val projectId: String,
    val fileId: String,
    enabled: Boolean,
    transitive: Boolean?,
    changing: Boolean?,
) : AbstractDependency(enabled, transitive, changing) {
    override fun toString() = "curse.maven:$projectName-$projectId:$fileId"
}
