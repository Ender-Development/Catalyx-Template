package dependency

abstract class AbstractDependency(val enabled: Boolean) {
    abstract override fun toString(): String
}
