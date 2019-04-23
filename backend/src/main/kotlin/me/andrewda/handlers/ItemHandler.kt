package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import me.andrewda.controllers.ItemController
import me.andrewda.models.NewItem
import me.andrewda.utils.respond

fun Route.item() {
    get("/items") {
        val items = ItemController.findAll()
        call.respond(items.map { it.api })
    }

    post("/items") {
        val newItem = call.receiveOrNull<NewItem>()

        if (newItem != null && newItem.isValid) {
            val user = ItemController.create(newItem)
            call.respond(user.api)
        } else {
            call.respond(status = HttpStatusCode.BadRequest)
        }
    }

    get("/items/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get

        val item = ItemController.findById(id)

        if (item != null) {
            call.respond(item.api)
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }

    patch("/items/{id}") {
        val id = (call.parameters["id"] ?: "").toIntOrNull() ?: return@patch
        val newItem = call.receiveOrNull<NewItem>()

        if (newItem == null) {
            call.respond(status = HttpStatusCode.BadRequest)
            return@patch
        }

        val item = ItemController.patch(id, newItem)

        if (item != null) {
            call.respond(item.api)
        } else {
            call.respond(status = HttpStatusCode.NotFound)
        }
    }
}
