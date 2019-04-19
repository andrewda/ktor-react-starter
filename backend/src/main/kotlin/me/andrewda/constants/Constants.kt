package me.andrewda.constants

object Constants {
    const val RESOURCE_DIRECTORY = "frontend"
    const val RESOURCE_STATIC_DIRECTORY = "$RESOURCE_DIRECTORY/static"
    const val RESOURCE_INDEX = "$RESOURCE_DIRECTORY/index.html"

    val BASE_RESOURCES = listOf(
        "favicon.ico",
        "manifest.json",
        "service-worker.js"
    )
}
