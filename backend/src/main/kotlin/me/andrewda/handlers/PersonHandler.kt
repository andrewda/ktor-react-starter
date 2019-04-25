package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receiveOrNull
import io.ktor.routing.*
import me.andrewda.controllers.PersonController
import me.andrewda.models.NewPerson
import me.andrewda.utils.MissingFields
import me.andrewda.utils.NotFound
import me.andrewda.utils.getApiResponse
import me.andrewda.utils.respond

fun Route.person() {
    route("/people") {
        get {
            val people = PersonController.findAll()
            call.respond(people.map { it.getApiResponse() })
        }

        get("/{slug}") {
            val slug = call.parameters["slug"] ?: throw NotFound()
            val person = PersonController.findBySlug(slug) ?: throw NotFound()

            call.respond(person.getApiResponse())
        }

        authenticate {
            post {
                val newPerson = call.receiveOrNull<NewPerson>() ?: throw MissingFields()

                if (newPerson.isValid) {
                    val request = PersonController.create(newPerson)

                    call.respond(request.getApiResponse())
                } else {
                    throw MissingFields()
                }
            }

            patch("/{slug}") {
                val slug = call.parameters["slug"] ?: throw NotFound()
                val newPerson = call.receiveOrNull<NewPerson>() ?: throw MissingFields()

                val person = PersonController.patch(slug, newPerson) ?: throw NotFound()

                call.respond(person.getApiResponse())
            }
        }
    }
}
