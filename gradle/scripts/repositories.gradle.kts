repositories {
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
        name = "Ender-Development Maven"
        url = uri("https://maven.ender-development.org/")
    }
    mavenLocal() // Must be last for caching to work
}