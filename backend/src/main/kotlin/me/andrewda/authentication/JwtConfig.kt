package me.andrewda.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import me.andrewda.models.User
import java.util.*

object JwtConfig {
    private const val issuer = "payitforward.com"
    private const val validity = 5 * 24 * 60 * 60 * 1000 // 5 days
    private val secret = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().toByteArray())
    private val algorithm = Algorithm.HMAC512(secret)

    /**
     * Calculate the expiration Date based on current time + the given validity.
     */
    private val expiration get() = Date(System.currentTimeMillis() + validity)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    /**
     * Produce a token for this [user].
     */
    fun makeToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id.value)
        .withExpiresAt(expiration)
        .sign(algorithm)
}
