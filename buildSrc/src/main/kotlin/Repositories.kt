import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

fun Project.loadDefaultRepositories() {
    repositories {
        mavenCentral()
        maven {
            name = "CleanroomMC Maven"
            url = uri("https://maven.cleanroommc.com")
        }
        maven {
            name = "SpongePowered Maven"
            url = uri("https://repo.spongepowered.org/maven")
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "CurseMaven"
                    url = uri("https://curse.cleanroommc.com")
                }
            }
            filter {
                includeGroup("curse.maven")
            }
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "Modrinth"
                    url = uri("https://api.modrinth.com/maven")
                }
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
        maven {
            name = "BlameJared's Maven"
            url = uri("https://maven.blamejared.com/")
        }
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
        }
        maven {
            name = "Ender-Development Maven"
            url = uri("https://maven.ender-development.org/")
        }
        maven {
            name = "GTNH Maven"
            url = uri("https://nexus.gtnewhorizons.com/repository/public/")
        }
        maven {
            name = "GTCEu Maven"
            url = uri("https://maven.gtceu.com")
        }
        mavenLocal() // Must be last for caching to work
    }
}
