package me.andrewda.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityID
import kotlin.reflect.full.memberProperties

enum class ReadLevel {
    USER, SELF, ADMIN
}

/**
 * Marks a field as readable in the API response.
 *
 * @param key the key to use in the response (defaults to the variable name)
 * @param readLevel the [ReadLevel] required to read this value
 * @param deep whether this field is a deep (performs a database query)
 */
annotation class Readable(
    val key: String = "",
    val readLevel: ReadLevel = ReadLevel.USER,
    val deep: Boolean = false
)

/**
 * Gets the [ApiResponse] generated for the [Entity] given a specific [readLevel].
 *
 * @param readLevel the permission level at which the response should be generated
 * @return the generated [ApiResponse]
 */
fun Entity<*>.getApiResponse(readLevel: ReadLevel = ReadLevel.USER): ApiResponse {
    val result = mutableMapOf<String, Any?>(
        "id" to null
    )

    this::class.memberProperties.forEach {
        val annotation = it.annotations.find { it is Readable } as? Readable

        val key = if (annotation?.key.isNullOrEmpty()) {
            it.name
        } else {
            annotation?.key ?: ""
        }

        if (key == "id" || annotation != null && readLevel >= annotation.readLevel && !annotation.deep) {
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
 * Gets the deep [ApiResponse] generated for the [Entity] given a specific [readLevel]. This method is different than
 * the typical [getApiResponse] in that this will also perform a database query for any values marked as
 * [Readable.deep].
 *
 * By default, this method will not call [getDeepApiResponse] on a child [Entity], but this behavior can be changed with
 * the [additionalDepth] parameter. Setting [additionalDepth] to a positive integer will perform [getDeepApiResponse] on
 * any [Entity] children down to that many layers. Alternatively, [additionalDepth] can be set to `-1` in order to
 * always use [getDeepApiResponse].
 *
 * @param readLevel the permission level at which the response should be generated
 * @param additionalDepth the additional number of layers on which to perform [getDeepApiResponse] (0 = none, -1 = all)
 * @return the generated [ApiResponse]
 */
suspend fun Entity<*>.getDeepApiResponse(readLevel: ReadLevel = ReadLevel.USER, additionalDepth: Int = 0): ApiResponse {
    val result = getApiResponse(readLevel)

    val jobs = mutableListOf<Job>()

    query {
        this::class.memberProperties.forEach {
            val annotation = it.annotations.find { it is Readable } as? Readable ?: return@forEach

            val key = if (annotation.key.isEmpty()) {
                it.name
            } else {
                annotation.key
            }


            if (readLevel >= annotation.readLevel && annotation.deep) {
                val value = it.getter.call(this)

                if (value is Entity<*>) {
                    if (additionalDepth == 0) {
                        result += key to value.getApiResponse(readLevel = readLevel)
                    } else {
                        jobs += GlobalScope.launch {
                            result += key to value.getDeepApiResponse(readLevel, additionalDepth - 1)
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
