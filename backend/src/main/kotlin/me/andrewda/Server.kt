package me.andrewda

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import me.andrewda.authentication.JwtConfig
import me.andrewda.constants.Routes
import me.andrewda.controllers.UserPrincipal
import me.andrewda.handlers.*
import me.andrewda.utils.Database
import me.andrewda.utils.ExceptionWithStatus
import me.andrewda.utils.Status
import me.andrewda.utils.userPrincipal
import org.slf4j.event.Level

fun Application.main() {
    Database.init()

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            validate {
                it.payload.getClaim("id").asInt()?.let { id ->
                    UserPrincipal(id)
                }
            }
        }
    }

    install(CallLogging) {
        level = Level.INFO
    }

    install(StatusPages) {
        status {
            call.respond(Status.fromHttpStatusCode(it))
        }

        exception<ExceptionWithStatus> { cause ->
            call.respond(cause.status, Status(
                cause.status.value,
                cause.message ?: cause.status.description,
                false
            ))
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
            auth()
            user()
            person()
            item()
            request()

            test()

            authenticate {
                authenticate {
                    route("secret") {
                        get {
                            val user = call.userPrincipal!!
                            call.respond(user.id)
                        }
                    }
                }
            }

            // Route any unspecified API requests to 404
            get("{...}") {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        frontend()
    }
}
