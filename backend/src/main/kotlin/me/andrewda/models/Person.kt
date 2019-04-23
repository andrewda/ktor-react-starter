package me.andrewda.models

import com.google.gson.annotations.Expose
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

data class NewPerson(
    @Expose val name: String?,
    @Expose val image: String?,
    @Expose val bio: String?,
    @Expose val slug: String?
) {
    val isValid get() = name != null
}

object People : IntIdTable() {
    val name = varchar("name", 20)
    val image = blob("image").nullable()
    val bio = text("bio")
    val slug = varchar("slug", 20).uniqueIndex()
}

class Person(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Person>(People) {
        data class ApiPerson(
            @Expose val id: Int,
            @Expose val name: String,
            @Expose val image: String?,
            @Expose val bio: String
        )
    }

    var name by People.name
    var image by People.image
    var bio by People.bio
    var slug by People.slug

    val api get() = ApiPerson(id.value, name, image?.toString(), bio)
}
