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

    suspend fun findAll() = query { User.all().toList() }

    suspend fun findByUsername(username: String) = query {
        User.find { Users.username eq username }.firstOrNull()
    }
}
