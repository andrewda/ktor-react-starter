package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import me.andrewda.controllers.UserController
import me.andrewda.models.NewUser
import me.andrewda.utils.getApiResponse
import me.andrewda.utils.respond

fun Route.user() {
    get("/users") {
        val users = UserController.findAll()
        call.respond(users.map { it.getApiResponse() })
    }

    post("/users") {
        val newUser = call.receiveOrNull<NewUser>()

        if (newUser != null && newUser.isValid && newUser.isFormatted) {
            val user = UserController.create(newUser)
            call.respond(user.getApiResponse())
        } else {
            call.respond(status = HttpStatusCode.BadRequest)
        }
    }

    get("/users/{username}") {
        val username = call.parameters["username"] ?: return@get
        val user = UserController.findByUsername(username)

        if (user != null) {
            call.respond(user.getApiResponse())
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }

    patch("/users/{username}") {
        val username = call.parameters["username"] ?: ""
        val newUser = call.receiveOrNull<NewUser>()

        if (newUser == null) {
            call.respond(status = HttpStatusCode.BadRequest)
            return@patch
        }

        val user = UserController.patch(username, newUser)

        if (user != null) {
            call.respond(user.getApiResponse())
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }
}
