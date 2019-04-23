package me.andrewda.controllers

import me.andrewda.models.Items
import me.andrewda.models.NewRequest
import me.andrewda.models.People
import me.andrewda.models.Request
import me.andrewda.utils.query
import org.jetbrains.exposed.dao.EntityID

object RequestController {
    suspend fun create(request: NewRequest) = query {
        if (request.person == null || request.item == null) {
            return@query null
        }

        Request.new {
            personId = EntityID(request.person, People)
            itemId = EntityID(request.item, Items)

            if (request.quantity != null) {
                quantity = request.quantity
            }

            if (request.fulfilled != null) {
                fulfilled = request.fulfilled
            }
        }
    }

    suspend fun patch(id: Int, newRequest: NewRequest) = query {
        val request = Request.findById(id) ?: return@query null

        if (newRequest.quantity != null) request.quantity = newRequest.quantity
        if (newRequest.fulfilled != null) request.fulfilled = newRequest.fulfilled

        request
    }

    suspend fun findAll() = query { Request.all().toList() }

    suspend fun findById(id: Int) = query { Request.findById(id) }
}
