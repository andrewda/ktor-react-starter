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
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import me.andrewda.constants.Constants.RESOURCE_INDEX
import me.andrewda.constants.Constants.RESOURCE_STATIC
import me.andrewda.constants.Constants.STATIC_ROUTE
import me.andrewda.constants.Endpoints.API
import me.andrewda.constants.Endpoints.ApiEndpoints
import me.andrewda.constants.Endpoints.ROOT
import org.slf4j.event.Level

fun Application.main() {
    install(CallLogging) {
        level = Level.INFO
    }

    routing {
        route(API) {
            get(ApiEndpoints.PING) {
                call.respondText("pong", ContentType.Text.Plain)
            }
        }

        static {
            resource(ROOT, RESOURCE_INDEX)

            static(STATIC_ROUTE) {
                resources(RESOURCE_STATIC)
            }
        }
    }
}
