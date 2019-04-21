package me.andrewda.handlers

import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import me.andrewda.utils.respond

fun Route.test() {
    get("/ping") {
        call.respond("pong")
    }
}
