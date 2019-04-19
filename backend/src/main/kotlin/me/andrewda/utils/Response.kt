package me.andrewda.utils

import com.google.gson.annotations.Expose
import io.ktor.http.HttpStatusCode

data class Status(
    @Expose val success: Boolean,
    @Expose val status: Int,
    @Expose val description: String
) {
    companion object {
        fun fromHttpStatusCode(code: HttpStatusCode) =
            Status(code.value < 400, code.value, code.description)
    }
}

data class Response<T>(
    @Expose private val success: Boolean,
    @Expose private val response: T
) {
    constructor(response: T) : this(true, response)
}
