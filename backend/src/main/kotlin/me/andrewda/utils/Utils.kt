package me.andrewda.utils

typealias ApiResponse = MutableMap<String, Any?>

fun CharSequence?.containsOrFalse(other: CharSequence, ignoreCase: Boolean = false) =
    this?.contains(other, ignoreCase) ?: false
