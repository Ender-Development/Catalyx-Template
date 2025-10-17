package dependency

open class Modrinth(enabled: Boolean, open var projectId: String, open var fileId: String) : AbstractDependency(enabled) {
    override fun toString() = "maven.modrinth:$projectId:$fileId"
}
