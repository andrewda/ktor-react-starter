package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.routing.*
import me.andrewda.controllers.PersonController
import me.andrewda.models.NewPerson
import me.andrewda.utils.respond

fun Route.person() {
    get("/people") {
        val people = PersonController.findAll()
        call.respond(people.map { it.api })
    }

    post("/people") {
        val newPerson = call.receiveOrNull<NewPerson>()

        if (newPerson != null && newPerson.isValid) {
            val request = PersonController.create(newPerson)

            call.respond(request.api)
        } else {
            call.respond(status = HttpStatusCode.BadRequest)
        }
    }

    get("/people/{slug}") {
        val slug = call.parameters["slug"] ?: return@get

        val person = PersonController.findBySlug(slug)

        if (person != null) {
            call.respond(person.api)
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }

    patch("/people/{slug}") {
        val slug = call.parameters["slug"] ?: return@patch
        val newPerson = call.receiveOrNull<NewPerson>()

        if (newPerson == null) {
            call.respond(status = HttpStatusCode.BadRequest)
            return@patch
        }

        val person = PersonController.patch(slug, newPerson)

        if (person != null) {
            call.respond(person.api)
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }
}
