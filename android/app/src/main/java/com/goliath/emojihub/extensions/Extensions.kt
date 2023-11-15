package com.goliath.emojihub.extensions

fun String.toEmoji(): String {
    return try {
        this.trim().split(" ").map { it.removePrefix("U+").toInt(16) }
            .joinToString("") { Character.toChars(it).joinToString("") }
    } catch (e: Exception){
        "\u2764\uFE0F"
    }
}