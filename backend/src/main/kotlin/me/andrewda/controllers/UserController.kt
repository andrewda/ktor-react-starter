package me.andrewda.controllers

import me.andrewda.models.NewUser
import me.andrewda.models.User
import me.andrewda.models.Users
import me.andrewda.utils.query

object UserController {
    suspend fun create(user: NewUser) = query {
        User.new {
            username = user.username ?: ""
            name = user.name ?: ""
            email = user.email ?: ""
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

    suspend fun findByUsername(username: String) = query {
        User.find { Users.username eq username }.firstOrNull()
    }
}
