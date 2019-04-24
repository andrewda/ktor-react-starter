package me.andrewda.controllers

import me.andrewda.models.Item
import me.andrewda.models.NewItem
import me.andrewda.utils.Database
import me.andrewda.utils.query

object ItemController {
    suspend fun create(item: NewItem) = query {
        val byteContent = item.image?.toByteArray()
        val blob = if (byteContent != null) {
            Database.connection.connector().createBlob().apply {
                setBytes(1, byteContent)
            }
        } else {
            null
        }

        Item.new {
            name = item.name ?: ""
            image = blob
            price = item.price ?: 0.0
            inventory = item.inventory
        }
    }

    suspend fun patch(id: Int, newItem: NewItem) = query {
        val item = Item.findById(id) ?: return@query null

        if (newItem.name != null) item.name = newItem.name
        if (newItem.price != null) item.price = newItem.price
        if (newItem.inventory != null) item.inventory = newItem.inventory
        if (newItem.image != null) {
            val byteContent = newItem.image.toByteArray()
            val blob = Database.connection.connector().createBlob().apply {
                setBytes(1, byteContent)
            }

            item.image = blob
        }

        item
    }

    suspend fun findAll() = query { Item.all().toList() }

    suspend fun findById(id: Int) = query { Item.findById(id) }
}
