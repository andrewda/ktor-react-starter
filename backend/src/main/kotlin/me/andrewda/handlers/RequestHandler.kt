package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import me.andrewda.controllers.RequestController
import me.andrewda.models.NewRequest
import me.andrewda.utils.getDeepApiResponse
import me.andrewda.utils.respond

fun Route.request() {
    get("/requests") {
        val requests = RequestController.findAll()
        call.respond(requests.map { it.getDeepApiResponse() })
    }

    post("/requests") {
        val newRequest = call.receiveOrNull<NewRequest>()

        if (newRequest != null && newRequest.isValid) {
            val request = RequestController.create(newRequest)

            if (request != null) {
                call.respond(request.getDeepApiResponse())
            } else {
                call.respond(status = HttpStatusCode.BadRequest)
            }
        } else {
            call.respond(status = HttpStatusCode.BadRequest)
        }
    }

    get("/requests/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get

        val request = RequestController.findById(id)

        if (request != null) {
            call.respond(request.getDeepApiResponse())
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }

    patch("/requests/{id}") {
        val id = (call.parameters["id"] ?: "").toIntOrNull() ?: return@patch
        val newRequest = call.receiveOrNull<NewRequest>()

        if (newRequest == null) {
            call.respond(status = HttpStatusCode.BadRequest)
            return@patch
        }

        val request = RequestController.patch(id, newRequest)

        if (request != null) {
            call.respond(request.getDeepApiResponse())
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }
}
