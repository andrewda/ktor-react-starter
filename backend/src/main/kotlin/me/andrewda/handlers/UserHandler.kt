package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import me.andrewda.controllers.UserController
import me.andrewda.models.NewUser
import me.andrewda.utils.Response

fun Route.userApi() {
    get("/users") {
        val users = UserController.findAll()

        call.respond(Response(users.map { it.api }))
    }

    get("/users/{username}") {
        val username = call.parameters["username"] ?: ""
        val user = UserController.findByUsername(username)

        if (user != null) {
            call.respond(Response(user.api))
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    post("/users") {
        val newUser = call.receiveOrNull<NewUser>()

        if (newUser != null && newUser.isValid) {
            val user = UserController.create(newUser)

            call.respond(Response(user.api))
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
