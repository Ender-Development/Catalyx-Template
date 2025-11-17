package util

import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import plugins.Logger
import plugins.Secrets
import java.io.File
import java.net.SocketTimeoutException
import java.net.URI
import java.net.UnknownHostException

object OnlineUtils {
    const val TEMPLATE_REPO = "Ender-Development/Catalyx-Template"
    const val TEMPLATE_BRANCH = "master"
    const val GITHUB_RAW_URL = "https://raw.githubusercontent.com"
    const val CONNECTION_TIMEOUT = 5000 // 5 seconds

    fun shouldDisableSync(): Boolean {
        if (isTemplateProject()) {
            Logger.info("Current project is the template project, skipping sync.")
            return true
        }

        if (Secrets.getOrEnvironment("SYNC_TEMPLATE")?.toBoolean() == false) {
            Logger.info("SYNC_TEMPLATE is set to false, skipping sync.")
            return true
        }

        return false
    }

    fun isTemplateProject(): Boolean {
        val repo = FileRepositoryBuilder()
            .setGitDir(File(".git"))
            .readEnvironment()
            .findGitDir()
            .build()
        val remoteUrl = repo.config.getString("remote", "origin", "url")
        Logger.info("Remote URL detected: $remoteUrl")
        return remoteUrl.contains("Ender-Development/Catalyx-Template")
    }

    fun isOnline(): Boolean {
        try {
            val connection = URI.create("https://api.github.com").toURL().openConnection()
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.readTimeout = CONNECTION_TIMEOUT
            connection.connect()
            connection.inputStream.close()
            Logger.info("Internet connection detected.")
            return true
        } catch (e: UnknownHostException) {
            Logger.error("No internet connection: ${e.message}")
            return false
        } catch (e: SocketTimeoutException) {
            Logger.error("Connection timed out: ${e.message}")
            return false
        } catch (e: Exception) {
            Logger.error("Error checking internet connection: ${e.message}")
            return false
        }
    }
}
