package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import util.OnlineUtils
import util.OnlineUtils.isOnline
import util.OnlineUtils.shouldDisableSync

class ScriptSync : Plugin<Project> {
    companion object {
        private lateinit var project: Project

        private val syncScripts: List<String> = listOf(
            "buildSrc/src/main/kotlin/plugins/DepLoader.kt",
            "buildSrc/src/main/kotlin/plugins/Loader.kt",
            "buildSrc/src/main/kotlin/plugins/Logger.kt",
            "buildSrc/src/main/kotlin/plugins/PropSync.kt",
            "buildSrc/src/main/kotlin/plugins/ReferenceCreator.kt",
            "buildSrc/src/main/kotlin/plugins/ScriptSync.kt",
            "buildSrc/src/main/kotlin/plugins/Secrets.kt",
            "buildSrc/src/main/kotlin/util/DependencyProvider.kt",
            "buildSrc/src/main/kotlin/util/OnlineUtils.kt",
            "buildSrc/src/main/kotlin/BaseSetup.kt",
            "buildSrc/src/main/kotlin/Dependencies.kt",
            "buildSrc/src/main/kotlin/PropertyExtension.kt",
            "buildSrc/src/main/kotlin/Repositories.kt",
            "buildSrc/build.gradle.kts",
            "build.gradle.kts",
            "settings.gradle.kts",
        )

        fun syncFilesFromTemplate() {
            Logger.banner("Searching for Files to sync!")
            if (shouldDisableSync()) return Logger.info("Sync is disabled via system.")
            if (!isOnline()) return Logger.warn("No internet connection detected.")
            performSync()
        }

        private fun performSync() {
            val baseUrl = "${OnlineUtils.GITHUB_RAW_URL}/${OnlineUtils.TEMPLATE_REPO}/${OnlineUtils.TEMPLATE_BRANCH}/"
            syncScripts.forEach {
                val fileUrl = "$baseUrl$it"
                val remoteContent = OnlineUtils.fetchFileContent(fileUrl) ?: throw Exception("Failed to fetch content from $fileUrl")
                val localFile = project.file(it)
                val localContent = if (localFile.exists()) localFile.readText() else ""

                if (remoteContent != localContent) {
                    localFile.writeText(remoteContent)
                    Logger.info("Synchronized file: $it")
                } else {
                    Logger.info("File is up-to-date: $it")
                }
            }
        }
    }

    override fun apply(target: Project) {
        project = target
        Logger.greet(this)
    }
}
