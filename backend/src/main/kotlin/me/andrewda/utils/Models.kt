package me.andrewda.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.andrewda.authentication.AuthLevel
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityID
import kotlin.reflect.full.memberProperties

/**
 * Marks a field as readable in the API response.
 *
 * @param key the key to use in the response (defaults to the variable name)
 * @param auth the [AuthLevel] required to read this value
 * @param deep whether this field is a deep (performs a database query)
 */
annotation class Readable(
    val key: String = "",
    val auth: AuthLevel = AuthLevel.USER,
    val deep: Boolean = false
)

/**
 * Gets the [ApiResponse] generated for the [Entity] given a specific [authLevel].
 *
 * @param authLevel the permission level at which the response should be generated
 * @return the generated [ApiResponse]
 */
fun Entity<*>.getApiResponse(
    authLevel: AuthLevel = AuthLevel.USER,
    exclude: List<String> = emptyList()
): ApiResponse {
    val result = mutableMapOf<String, Any?>()

    this::class.memberProperties.forEach {
        val annotation = it.annotations.find { it is Readable } as? Readable

        val key = if (annotation?.key.isNullOrEmpty()) {
            it.name
        } else {
            annotation?.key ?: ""
        }

        if (exclude.contains(key)) return@forEach

        if (key == "id" || annotation != null && authLevel >= annotation.auth && !annotation.deep) {
            val value = it.getter.call(this)

            result += if (value is EntityID<*>) {
                key to value.value
            } else {
                key to value
            }
        }
    }

    return result
}

/**
 * Gets the deep [ApiResponse] generated for the [Entity] given a specific [authLevel]. This method is different than
 * the typical [getApiResponse] in that this will also perform a database query for any values marked as
 * [Readable.deep].
 *
 * By default, this method will not call [getDeepApiResponse] on a child [Entity], but this behavior can be changed with
 * the [additionalDepth] parameter. Setting [additionalDepth] to a positive integer will perform [getDeepApiResponse] on
 * any [Entity] children down to that many layers. Alternatively, [additionalDepth] can be set to `-1` in order to
 * always use [getDeepApiResponse].
 *
 * @param authLevel the permission level at which the response should be generated
 * @param additionalDepth the additional number of layers on which to perform [getDeepApiResponse] (0 = none, -1 = all)
 * @return the generated [ApiResponse]
 */
suspend fun Entity<*>.getDeepApiResponse(
    authLevel: AuthLevel = AuthLevel.USER,
    additionalDepth: Int = 0,
    exclude: List<String> = emptyList()
): ApiResponse {
    val result = getApiResponse(authLevel, exclude)

    val jobs = mutableListOf<Job>()

    query {
        this::class.memberProperties.forEach {
            val annotation = it.annotations.find { it is Readable } as? Readable ?: return@forEach

            val key = if (annotation.key.isEmpty()) {
                it.name
            } else {
                annotation.key
            }

            if (exclude.contains(key)) return@forEach

            if (authLevel >= annotation.auth && annotation.deep) {
                val value = it.getter.call(this)

                if (value is Entity<*>) {
                    if (additionalDepth == 0) {
                        result += key to value.getApiResponse(authLevel = authLevel)
                    } else {
                        jobs += GlobalScope.launch {
                            result += key to value.getDeepApiResponse(authLevel, additionalDepth - 1)
                        }
                    }
                } else {
                    result += key to value
                }
            }
        }
    }

    jobs.forEach { it.join() }

    return result
}
