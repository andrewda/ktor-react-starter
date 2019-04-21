package me.andrewda.models

import com.google.gson.annotations.Expose
import me.andrewda.utils.containsOrFalse
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

data class NewUser(
    @Expose val username: String?,
    @Expose val name: String?,
    @Expose val email: String?,
    @Expose val password: String?
) {
    val isValid get() = username != null && name != null && email != null && password != null
    val isFormatted get() = !username.containsOrFalse(" ") && !email.containsOrFalse(" ")
}

object Users : IntIdTable() {
    val username = varchar("username", 20).uniqueIndex()
    val name = varchar("name", 50)
    val email = varchar("email", 50)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        data class ApiUser(
            @Expose val id: Int,
            @Expose val username: String,
            @Expose val name: String,
            val email: String
        )
    }

    var username by Users.username
    var name by Users.name
    var email by Users.email

    val api get() = ApiUser(id.value, username, name, email)
}
