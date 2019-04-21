package me.andrewda

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import me.andrewda.constants.Routes
import me.andrewda.handlers.frontend
import me.andrewda.handlers.testApi
import me.andrewda.handlers.userApi
import me.andrewda.utils.Database
import me.andrewda.utils.Status
import org.slf4j.event.Level

fun Application.main() {
    Database.init()

    install(CallLogging) {
        level = Level.INFO
    }

    install(StatusPages) {
        status(HttpStatusCode.NotFound, HttpStatusCode.InternalServerError) {
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
            userApi()
            testApi()

            // Route any unspecified API requests to 404
            get("{...}") {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        frontend()
    }
}
