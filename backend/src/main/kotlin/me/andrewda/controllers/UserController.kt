package me.andrewda.controllers

import com.google.gson.annotations.Expose
import io.ktor.auth.Principal
import me.andrewda.authentication.checkPassword
import me.andrewda.authentication.hashPassword
import me.andrewda.models.NewUser
import me.andrewda.models.User
import me.andrewda.models.Users
import me.andrewda.utils.query
import org.jetbrains.exposed.sql.or

data class UserPrincipal(
    val id: Int
) : Principal

data class UserPasswordCredential(
    @Expose val identifier: String,
    @Expose val password: String
)

object UserController {
    suspend fun create(user: NewUser) = query {
        User.new {
            username = user.username ?: ""
            name = user.name ?: ""
            email = user.email ?: ""
            password = hashPassword(user.password ?: "")
        }
    }

    suspend fun patch(username: String, newUser: NewUser) = query {
        val user = User.find { Users.username eq username }.firstOrNull() ?: return@query null

        if (newUser.username != null) user.username = newUser.username
        if (newUser.name != null) user.name = newUser.name
        if (newUser.email != null) user.email = newUser.email

        user
    }

    suspend fun findAll() = query { User.all().toList() }

    suspend fun findById(id: Int) = query {
        User.findById(id)
    }

    suspend fun findByUsername(username: String) = query {
        User.find { Users.username eq username }.firstOrNull()
    }

    suspend fun findByCredentials(credential: UserPasswordCredential) = query {
        val users = User.find {
            (Users.username eq credential.identifier) or (Users.email eq credential.identifier)
        }

        users.find { checkPassword(credential.password, it) }
    }
}
