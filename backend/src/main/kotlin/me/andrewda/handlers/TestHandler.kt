package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import me.andrewda.utils.Response

fun Route.testApi() {
    get("/ping") {
        call.respond(Response("pong"))
    }
}
