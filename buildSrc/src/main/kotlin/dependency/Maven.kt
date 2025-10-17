package dependency

class Maven(enabled: Boolean, val path: String, val version: String) : AbstractDependency(enabled) {
    override fun toString() = "$path:$version"
}
