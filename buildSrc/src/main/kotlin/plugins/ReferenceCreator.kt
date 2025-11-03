package plugins

import evaluate
import org.gradle.api.Plugin
import org.gradle.api.Project
import propertyString
import java.io.File

class ReferenceCreator : Plugin<Project> {
    companion object {
        const val REFERENCE_FILE = "tags.properties"

        private fun createReference(project: Project) {
            val properties = Loader.loadPropertyFromFile(REFERENCE_FILE)
            val objectName = project.propertyString("mod_name").filter { it.isLetterOrDigit() }
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
                appendLine("object ${objectName}Reference {")
                properties.forEach { (key, value) ->
                    val eval = if (value is String) project.evaluate(value) else value
                    appendLine("    const val ${key.toString().uppercase()} = ${if (eval.toString().all { it.isDigit() } && eval.toString().count {it == '.'} <= 1) eval else "\"$eval\""}")
                }
                appendLine("}")
            }
            val outputFile = "src/main/kotlin/${objectPath.replace(".", "/")}/${objectName}Reference.kt"
            val dir = File(outputFile).parentFile
            if (!dir.exists()) {
                dir.mkdirs()
            }
            File(outputFile).writeText(referenceContent)
            Logger.info("Reference.kt created at $outputFile")
        }
    }

    override fun apply(target: Project) {
        Logger.greet(this)
        createReference(target)
    }
}
