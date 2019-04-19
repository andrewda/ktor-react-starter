package me.andrewda

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
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
import me.andrewda.utils.Response
import me.andrewda.utils.Status
import org.slf4j.event.Level

fun Application.main() {
    install(CallLogging) {
        level = Level.INFO
    }

    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(Status.fromHttpStatusCode(it))
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            enableComplexMapKeySerialization()
            excludeFieldsWithoutExposeAnnotation()
        }
    }

    routing {
        route(Routes.API) {
            get(ApiEndpoints.PING) {
                call.respond(Response("pong"))
            }

            // Route any unspecified API requests to 404
            get("{...}") {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        frontend()
    }
}

fun Route.frontend() {
    static("/") {
        BASE_RESOURCES.forEach {
            resource(it, "$RESOURCE_DIRECTORY/$it")
        }
    }

    static(Routes.STATIC) {
        resources(RESOURCE_STATIC_DIRECTORY)
    }

    static("{...}") {
        defaultResource(RESOURCE_INDEX)
    }
}
