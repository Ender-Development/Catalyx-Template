plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("secretsPlugin") {
            id = "catalyx.secrets"
            implementationClass = "plugins.Secrets"
        }
        create("loggerPlugin") {
            id = "catalyx.logger"
            implementationClass = "plugins.Logger"
        }
        create("loaderPlugin") {
            id = "catalyx.loader"
            implementationClass = "plugins.Loader"
        }
        create("depLoaderPlugin") {
            id = "catalyx.deploader"
            implementationClass = "plugins.DepLoader"
        }
    }
}
