package me.andrewda.handlers

import io.ktor.http.content.defaultResource
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.Route
import me.andrewda.constants.Constants
import me.andrewda.constants.Routes

fun Route.frontend() {
    static("/") {
        Constants.BASE_RESOURCES.forEach {
            resource(it, "${Constants.RESOURCE_DIRECTORY}/$it")
        }
    }

    static(Routes.STATIC) {
        resources(Constants.RESOURCE_STATIC_DIRECTORY)
    }

    static("{...}") {
        defaultResource(Constants.RESOURCE_INDEX)
    }
}
