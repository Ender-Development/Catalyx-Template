import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PluginLogger {
    private val logFile: File by lazy {
        val file = File("gradle/catalyx.log")
        file.delete()
        file
    }

    @Synchronized
    fun log(message: String) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        PrintWriter(FileWriter(logFile, true)).use { out ->
            out.println("[$timestamp] $message")
        }
    }
}