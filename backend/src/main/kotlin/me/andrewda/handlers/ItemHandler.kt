package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receiveOrNull
import io.ktor.routing.*
import me.andrewda.authentication.AuthLevel
import me.andrewda.controllers.ItemController
import me.andrewda.models.NewItem
import me.andrewda.utils.*

fun Route.item() {
    route("/items") {
        get {
            val items = ItemController.findAll()
            call.respond(items.map { it.getApiResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw NotFound()
            val item = ItemController.findById(id) ?: throw NotFound()

            call.respond(item.getApiResponse())
        }

        authenticate {
            post {
                call.ensureAuthLevel(AuthLevel.ADMIN)

                val newItem = call.receiveOrNull<NewItem>() ?: throw MissingFields()

                if (newItem.isValid) {
                    val user = ItemController.create(newItem)
                    call.respond(user.getApiResponse())
                } else {
                    throw MissingFields()
                }
            }

            patch("/{id}") {
                call.ensureAuthLevel(AuthLevel.ADMIN)

                val id = (call.parameters["id"] ?: "").toIntOrNull() ?: throw NotFound()
                val newItem = call.receiveOrNull<NewItem>() ?: throw MissingFields()

                val item = ItemController.patch(id, newItem) ?: throw NotFound()

                call.respond(item.getApiResponse())
            }
        }
    }
}
