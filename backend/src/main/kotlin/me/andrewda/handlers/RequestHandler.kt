package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receiveOrNull
import io.ktor.routing.*
import me.andrewda.controllers.RequestController
import me.andrewda.models.NewRequest
import me.andrewda.utils.MissingFields
import me.andrewda.utils.NotFound
import me.andrewda.utils.getDeepApiResponse
import me.andrewda.utils.respond

fun Route.request() {
    route("/requests") {
        get {
            val requests = RequestController.findAll()
            call.respond(requests.map { it.getDeepApiResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw NotFound()
            val request = RequestController.findById(id) ?: throw NotFound()

            call.respond(request.getDeepApiResponse())
        }

        authenticate {
            post {
                val newRequest = call.receiveOrNull<NewRequest>() ?: throw MissingFields()

                if (newRequest.isValid) {
                    val request = RequestController.create(newRequest) ?: throw MissingFields()

                    call.respond(request.getDeepApiResponse())
                } else {
                    throw MissingFields()
                }
            }

            patch("/{id}") {
                val id = (call.parameters["id"] ?: "").toIntOrNull() ?: return@patch
                val newRequest = call.receiveOrNull<NewRequest>() ?: throw MissingFields()

                val request = RequestController.patch(id, newRequest) ?: throw NotFound()

                call.respond(request.getDeepApiResponse())
            }
        }
    }
}
