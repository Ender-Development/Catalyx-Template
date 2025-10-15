object TemplateSync {
    private const val TEMPLATE_REPO = "Ender-Development/Catalyx-Template"
    private const val TEMPLATE_BRANCH = "master"
    private const val GITHUB_RAW_URL = "https://raw.githubusercontent.com"
    private const val CONNECTION_TIMEOUT = 5000 // 5 seconds

     // Configuration for what to sync
    private val syncConfig = mapOf(
        "dependencies.properties" to SyncConfig(
            keysToSync = listOf("assetmover_version", "catalyx_version", "configanytime_version", "forgelin_continuous_version", "mixin_booter_version", "modularui_version"),
            syncAll = false
        ),
        "integration.properties" to SyncConfig(
            keysToSync = listOf("crafttweaker_version", "groovyscript_version", "hei_version", "top_version"),
            syncAll = false
        ),
        "utilities.properties" to SyncConfig(
            keysToSync = listOf("ktfmt_version"),
            syncAll = false
        )
    )

     data class SyncConfig(
        val keysToSync: List<String>,
        val syncAll: Boolean = false
    )
}