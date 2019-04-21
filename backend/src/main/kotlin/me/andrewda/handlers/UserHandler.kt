package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import me.andrewda.controllers.UserController
import me.andrewda.models.NewUser
import me.andrewda.utils.respond

fun Route.user() {
    get("/users") {
        val users = UserController.findAll()
        call.respond(users.map { it.api })
    }

    get("/users/{username}") {
        val username = call.parameters["username"] ?: ""
        val user = UserController.findByUsername(username)

        if (user != null) {
            call.respond(user.api)
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }

    post("/users") {
        val newUser = call.receiveOrNull<NewUser>()

        if (newUser != null && newUser.isValid && newUser.isFormatted) {
            val user = UserController.create(newUser)
            call.respond(user.api)
        } else {
            call.respond(status = HttpStatusCode.BadRequest)
        }
    }
}
