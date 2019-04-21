package me.andrewda.utils

import com.google.gson.annotations.Expose
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode

data class Status(
    @Expose val status: Int,
    @Expose val description: String,
    @Expose val success: Boolean
) {
    companion object {
        fun fromHttpStatusCode(code: HttpStatusCode) =
            Status(code.value, code.description, code.value < 400)
    }
}

data class Response<T>(
    @Expose val response: T,
    @Expose val success: Boolean = true
)

suspend inline fun ApplicationCall.respond(
    message: Any? = null,
    status: HttpStatusCode = HttpStatusCode.OK,
    success: Boolean? = null
) {
    response.status(status)

    if (message is Response<*> || message == null) {
        response.pipeline.execute(this, message ?: status)
    } else {
        val responseSuccess = success ?: (status.value < 400)
        response.pipeline.execute(this, Response(message, success = responseSuccess))
    }
}
