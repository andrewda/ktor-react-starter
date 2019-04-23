package me.andrewda.models

import com.google.gson.annotations.Expose
import me.andrewda.utils.query
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
    companion object : IntEntityClass<Request>(Requests) {
        data class ApiRequest(
            @Expose val id: Int,
            @Expose val personId: Int,
            @Expose val itemId: Int,
            @Expose val quantity: Int,
            @Expose val fulfilled: Int,
            @Expose val complete: Boolean
        )

        data class ApiDeepRequest(
            @Expose val id: Int,
            @Expose val personId: Int,
            @Expose val person: Person.Companion.ApiPerson?,
            @Expose val itemId: Int,
            @Expose val item: Item.Companion.ApiItem?,
            @Expose val quantity: Int,
            @Expose val fulfilled: Int,
            @Expose val complete: Boolean,
            @Expose val totalPrice: Double
        )
    }

    var personId by Requests.person
    var itemId by Requests.item
    var quantity by Requests.quantity
    var fulfilled by Requests.fulfilled

    inline val person get() = Person.findById(personId)
    inline val item get() = Item.findById(itemId)

    val complete get() = fulfilled >= quantity
    inline val totalPrice get() = (item?.price ?: 0.0) * quantity

    val api get() = ApiRequest(id.value, personId.value, itemId.value, quantity, fulfilled, complete)

    suspend fun getDeepApi(populatePerson: Boolean = false, populateItem: Boolean = true) = query {
        val person = if (populatePerson) person?.api else null
        val item = if (populateItem) item?.api else null

        ApiDeepRequest(id.value, personId.value, person, itemId.value, item, quantity, fulfilled, complete, totalPrice)
    }
}
