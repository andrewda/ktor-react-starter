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
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*
import me.andrewda.constants.Constants.BASE_RESOURCES
import me.andrewda.constants.Constants.RESOURCE_DIRECTORY
import me.andrewda.constants.Constants.RESOURCE_INDEX
import me.andrewda.constants.Constants.RESOURCE_STATIC_DIRECTORY
import me.andrewda.constants.Routes
import me.andrewda.constants.Routes.ApiEndpoints
import me.andrewda.models.NewUser
import me.andrewda.models.User
import me.andrewda.models.Users
import me.andrewda.utils.Database
import me.andrewda.utils.Response
import me.andrewda.utils.Status
import me.andrewda.utils.query
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
            get(ApiEndpoints.PING) {
                call.respond(Response("pong"))
            }

            get("/users") {
                val users = query {
                    User.all().toList()
                }

                call.respond(Response(users.map { it.api }))
            }

            get("/users/{username}") {
                val username = call.parameters["username"] ?: ""
                val user = query {
                    User.find { Users.username eq username }.firstOrNull()
                }

                if (user != null) {
                    call.respond(Response(user.api))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post("/users") {
                val newUser = call.receiveOrNull<NewUser>()

                if (newUser != null && newUser.isValid) {
                    val user = query {
                        User.new {
                            username = newUser.username ?: ""
                            name = newUser.name ?: ""
                            email = newUser.email ?: ""
                        }
                    }

                    call.respond(Response(user.api))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
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
