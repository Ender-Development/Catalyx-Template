repositories {
    maven {
        name = "CleanroomMC Maven"
        url = uri("https://maven.cleanroommc.com")
    }
    maven {
        name = "SpongePowered Maven"
        url = uri("https://repo.spongepowered.org/maven")
    }

//    exclusiveContent {
//        forRepository {
//            maven {
//                name 'CurseMaven'
//                url 'https://curse.cleanroommc.com'
//            }
//        }
//        filter {
//            includeGroup 'curse.maven'
//        }
//    }
//    exclusiveContent {
//        forRepository {
//            maven {
//                name 'Modrinth'
//                url 'https://api.modrinth.com/maven'
//            }
//        }
//        filter {
//            includeGroup 'maven.modrinth'
//        }
//    }
//    maven {
//        url = 'https://maven.blamejared.com/'
//    }
//    maven {
//        url = 'https://repo.spongepowered.org/maven/'
//    }
    mavenLocal() // Must be last for caching to work
}