package me.andrewda.utils

import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import me.andrewda.authentication.AuthLevel
import me.andrewda.controllers.UserController
import me.andrewda.controllers.UserPrincipal
import me.andrewda.models.User

typealias ApiResponse = MutableMap<String, Any?>

fun CharSequence?.containsOrFalse(other: CharSequence, ignoreCase: Boolean = false) =
    this?.contains(other, ignoreCase) ?: false

val ApplicationCall.userPrincipal get() = authentication.principal<UserPrincipal>()

suspend fun ApplicationCall.getUser(): User? {
    val userId = userPrincipal?.id ?: return null
    return UserController.findById(userId)
}

fun ApplicationCall.isSelf(user: User?) = user?.id?.value == userPrincipal?.id

fun ApplicationCall.getAuthLevel(user: User?) = when {
    isSelf(user) && user?.authLevel == AuthLevel.ADMIN -> AuthLevel.ADMIN
    isSelf(user) -> AuthLevel.SELF
    else -> AuthLevel.USER
}

suspend fun ApplicationCall.ensureAuthLevel(authLevel: AuthLevel, user: User? = null, username: String? = null) {
    val userAuthLevel = if (user != null) {
        getAuthLevel(user)
    } else {
        val authUser = getUser()

        when {
            authUser?.authLevel == AuthLevel.ADMIN -> AuthLevel.ADMIN
            authUser?.username == username -> AuthLevel.SELF
            else -> AuthLevel.USER
        }
    }

    if (userAuthLevel < authLevel) throw Forbidden()
}
