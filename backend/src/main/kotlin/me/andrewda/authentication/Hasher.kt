package me.andrewda.authentication

import me.andrewda.models.User
import org.mindrot.jbcrypt.BCrypt

/**
 * Check if the password matches the User's password
 */
fun checkPassword(password: String, user: User) = BCrypt.checkpw(password, user.password)

/**
 * Returns the hashed version of the supplied password
 */
fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())
