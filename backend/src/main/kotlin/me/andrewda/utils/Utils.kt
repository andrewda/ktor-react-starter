package me.andrewda.utils

fun CharSequence?.containsOrFalse(other: CharSequence, ignoreCase: Boolean = false) =
    this?.contains(other, ignoreCase) ?: false
