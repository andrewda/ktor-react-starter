package me.andrewda.models

import com.google.gson.annotations.Expose
import me.andrewda.authentication.AuthLevel
import me.andrewda.utils.Readable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

data class NewRequest(
    @Expose val person: Int?,
    @Expose val item: Int?,
    @Expose val quantity: Int?,
    @Expose val fulfilled: Int?
) {
    val isValid get() = person != null && item != null && quantity != null
}

object Requests : IntIdTable() {
    val person = entityId("person", People)
    val item = entityId("item", Items)
    val quantity = integer("quantity").default(1)
    val fulfilled = integer("fulfilled").default(0)
}

class Request(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Request>(Requests)

    @Readable
    var personId by Requests.person

    @Readable
    var itemId by Requests.item

    @Readable
    var quantity by Requests.quantity

    @Readable(auth = AuthLevel.ADMIN)
    var fulfilled by Requests.fulfilled

    @Readable
    val complete get() = fulfilled >= quantity

    @Readable(deep = true)
    inline val person get() = Person.findById(personId)

    @Readable(deep = true)
    inline val item get() = Item.findById(itemId)

    @Readable(deep = true)
    inline val totalPrice get() = (item?.price ?: 0.0) * quantity
}
