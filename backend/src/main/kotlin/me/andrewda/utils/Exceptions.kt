package me.andrewda.utils

import io.ktor.http.HttpStatusCode

open class ExceptionWithStatus(
    message: String,
    val status: HttpStatusCode = HttpStatusCode.InternalServerError
) : Exception(message)

class InvalidCredentialException : ExceptionWithStatus("Username or password is incorrect", HttpStatusCode.Unauthorized)
