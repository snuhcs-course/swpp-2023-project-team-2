package com.goliath.emojihub.extensions

import android.util.Log

fun String.toEmoji(): String {
    return try {
        this.trim().split(" ").map { it.removePrefix("U+").toInt(16) }
            .joinToString("") { Character.toChars(it).joinToString("") }
    } catch (e: Exception){
        "\u2764\uFE0F"
    }
}

fun reactionsToString (reactions: List<String>): String {
    var emojisStr = ""
    if (reactions.size >= 3) {
        Log.d("emojiStr", "${reactions.size}")
        for (emoji in 0 until 3) {
            emojisStr += emoji
            emojisStr += " "
        }
        emojisStr += "외 ${reactions.size - 3}개의 반응"
    } else {
        Log.d("emojiStr", "${reactions.size}")
        for (emoji in reactions) {
            emojisStr += emoji
            emojisStr += " "
        }
        emojisStr += "개의 반응"
    }
    return emojisStr
}