package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import util.OnlineUtils.isOnline
import util.OnlineUtils.shouldDisableSync

class ScriptSync : Plugin<Project> {
    companion object {
        private var foundUpdate = false
        private lateinit var project: Project

        private val syncScripts: List<String> = listOf(
            "DepLoader.kt",
            "Loader.kt",
            "Logger.kt",
            "PropSync.kt",
            "ReferenceCreator.kt",
            "ScriptSync.kt",
            "Secrets.kt",
            "DependencyProvider.kt",
            "OnlineUtils.kt",
            "BaseSetup.kt",
            "Dependencies.kt",
            "PropertyExtension.kt",
            "Repositories.kt",
            "build.gradle.kts",
            "build.gradle.kts",
            "settings.gradle.kts",
        )

        fun syncFilesFromTemplate() {
            if (shouldDisableSync()) return Logger.info("Sync is disabled via system.")
            if (!isOnline()) return Logger.warn("No internet connection detected.")
            performSync()
        }

        private fun performSync() {
            Logger.banner("Searching for Files to sync!")
        }
    }

    override fun apply(target: Project) {
        project = target
        Logger.greet(this)
        performSync()
    }
}
