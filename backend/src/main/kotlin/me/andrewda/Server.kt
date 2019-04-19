package me.andrewda

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.http.ContentType
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import me.andrewda.constants.Constants.BASE_RESOURCES
import me.andrewda.constants.Constants.RESOURCE_DIRECTORY
import me.andrewda.constants.Constants.RESOURCE_INDEX
import me.andrewda.constants.Constants.RESOURCE_STATIC_DIRECTORY
import me.andrewda.constants.Routes
import me.andrewda.constants.Routes.ApiEndpoints
import org.slf4j.event.Level

fun Application.main() {
    install(CallLogging) {
        level = Level.INFO
    }

    routing {
        route(Routes.API) {
            get(ApiEndpoints.PING) {
                call.respondText("pong", ContentType.Text.Plain)
            }
        }

        frontend()
    }
}

fun Route.frontend() {
    static(Routes.STATIC) {
        resources(RESOURCE_STATIC_DIRECTORY)
    }

    static("/") {
        resource("/", RESOURCE_INDEX)

        BASE_RESOURCES.forEach {
            resource(it, "$RESOURCE_DIRECTORY/$it")
        }
    }

    static("*") {
        resource("/", RESOURCE_INDEX)
    }
}
