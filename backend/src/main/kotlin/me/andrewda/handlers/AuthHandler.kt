package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import me.andrewda.authentication.JwtConfig
import me.andrewda.controllers.UserController
import me.andrewda.controllers.UserPasswordCredential
import me.andrewda.utils.InvalidCredential
import me.andrewda.utils.respond

fun Route.auth() {
    route("/auth") {
        post("/login") {
            val credentials = call.receive<UserPasswordCredential>()
            val user = UserController.findByCredentials(credentials) ?: throw InvalidCredential()
            val token = JwtConfig.makeToken(user)
            call.respond(mapOf("token" to token))
        }
    }
}
