/*
Internal helper functions for Gradle build scripts.
Don't forget to apply the script where you want to use them:
apply(from = "gradle/scripts/helpers.gradle.kts")
 */

fun propertyString(key: String): String = project.property(key).toString()

fun propertyBoolean(key: String): Boolean = propertyString(key).toBoolean()

fun propertyStringList(key: String, delimit: String): List<String> =
    propertyString(key).split(delimit).map { it.trim() }.filter { it.isNotEmpty() }