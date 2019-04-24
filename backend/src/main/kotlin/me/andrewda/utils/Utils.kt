package me.andrewda.utils

import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import me.andrewda.controllers.UserPrincipal

typealias ApiResponse = MutableMap<String, Any?>

fun CharSequence?.containsOrFalse(other: CharSequence, ignoreCase: Boolean = false) =
    this?.contains(other, ignoreCase) ?: false

val ApplicationCall.user get() = authentication.principal<UserPrincipal>()
