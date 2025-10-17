package dependency

class Curseforge(enabled: Boolean, val projectName: String, override var projectId: String, override var fileId: String) :
    Modrinth(enabled, projectId, fileId) {
    override fun toString() = "curse.maven:$projectName-$projectId:$fileId"
}
