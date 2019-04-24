package me.andrewda.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import me.andrewda.models.User
import java.util.*

object JwtConfig {
    private const val secret = "zAP5MBA4B4Ijz0MZaS48"
    private const val issuer = "payitforward.com"
    private const val validity = 5 * 24 * 60 * 60 * 1000 // 5 days
    private val algorithm = Algorithm.HMAC512(secret)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private val expiration get() = Date(System.currentTimeMillis() + validity)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id.value)
        .withExpiresAt(expiration)
        .sign(algorithm)
}
