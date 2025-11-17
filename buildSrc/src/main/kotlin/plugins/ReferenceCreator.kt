package plugins

import evaluate
import org.gradle.api.Plugin
import org.gradle.api.Project
import propertyString
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.writeText

class ReferenceCreator : Plugin<Project> {
    companion object {
        const val REFERENCE_FILE = "tags.properties"

        private fun createReference(project: Project) {
            val properties = Loader.loadPropertyFromFile(REFERENCE_FILE)
            val objectName = project.propertyString("mod_name").filter(Char::isLetterOrDigit)
            val objectPath = "${project.propertyString("root_package")}.${project.propertyString("mod_id")}"
            val referenceContent = buildString {
                appendLine("package $objectPath")
                appendLine()
                appendLine("internal typealias Reference = ${objectName}Reference")
                appendLine()
                appendLine("/**")
                appendLine(" * Auto-generated reference object containing constants from `$REFERENCE_FILE`.")
                appendLine(" * Don't change this file manually as it will be overwritten.")
                appendLine(" */")
                appendLine("@Suppress(\"UNUSED\")")
                appendLine("object ${objectName}Reference {")
                properties.forEach { (key, value) ->
                    val eval = (if (value is String) project.evaluate(value) else value).toString()
                    appendLine("    const val ${key.toString().uppercase()} = ${if (eval.all(Char::isDigit) && eval.count { it == '.' } <= 1) eval else "\"$eval\""}")
                }
                appendLine("}")
            }.replace("\r\n", "\n") // Normalize line endings
            val outputPath = "src/main/kotlin/${project.propertyString("tags_package").replace(".", "/")}/${objectName}Reference.kt"
            val outputFile = Path(outputPath)
            if(outputFile.notExists()) {
                outputFile.parent.createDirectories()
                outputFile.createFile()
            }

            outputFile.writeText(referenceContent)
            Logger.info("Reference.kt created at $outputPath")
        }
    }

    override fun apply(target: Project) {
        Logger.greet(this)
        createReference(target)
    }
}
