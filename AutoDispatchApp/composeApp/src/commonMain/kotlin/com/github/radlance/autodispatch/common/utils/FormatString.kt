package com.github.radlance.autodispatch.common.utils

fun abbreviateName(fullName: String): String {
    val words = fullName.trim().split("\\s+".toRegex())
    if (words.isEmpty()) return ""
    val firstWord = words.first()
    val initials =
        words.drop(1).joinToString(" ") { it.firstOrNull()?.uppercaseChar()?.toString() + "." }
    return if (initials.isEmpty()) firstWord else "$firstWord $initials"
}

fun avatarInitials(input: String): String {
    if (input.isBlank()) return "-"

    val words = input
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

    if (words.isEmpty()) return "-"

    return words
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "-" }
}
