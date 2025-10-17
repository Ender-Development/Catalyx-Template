package dependency

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
