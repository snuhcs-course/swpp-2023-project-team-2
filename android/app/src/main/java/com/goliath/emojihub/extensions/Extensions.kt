package com.goliath.emojihub.extensions

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

    if (reactions.isEmpty()) {
        emojisStr = "아직 반응이 없습니다."
    } else if (reactions.size >= 3) {
        for (emoji in 0 until 3) {
            emojisStr += emoji
            emojisStr += " "
        }
        emojisStr += "외 ${reactions.size - 3}개의 반응"
    } else {
        for (emoji in reactions) {
            emojisStr += emoji
            emojisStr += " "
        }
        emojisStr += "개의 반응"
    }
    return emojisStr
}