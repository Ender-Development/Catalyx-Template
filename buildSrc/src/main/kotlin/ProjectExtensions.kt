import org.gradle.api.Project

// Your hello function moved to buildSrc
fun Project.hello(name: String) {
    project.logger.info("Hello $name")
}
