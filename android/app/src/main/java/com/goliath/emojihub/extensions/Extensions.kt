package com.goliath.emojihub.extensions

import com.goliath.emojihub.models.ReactionWithEmojiUnicode

fun String.toEmoji(): String {
    return try {
        this.trim().split(" ").map { it.removePrefix("U+").toInt(16) }
            .joinToString("") { Character.toChars(it).joinToString("") }
    } catch (e: Exception){
        "\u2764\uFE0F"
    }
}

fun reactionsToString (reactions: List<ReactionWithEmojiUnicode>): String {
    var emojisStr = ""

    if (reactions.size >= 3) {
        val lastThreeReactions = reactions.takeLast(3)
        for (reaction in lastThreeReactions) {
            emojisStr += reaction.emoji_unicode.toEmoji()
            emojisStr += " "
        }
        emojisStr += if (reactions.size == 3) "3개의 반응"
        else "외 ${reactions.size - 3}개의 반응"
    } else {
        for (reaction in reactions) {
            emojisStr += reaction.emoji_unicode.toEmoji()
            emojisStr += " "
        }
        emojisStr += "${reactions.size}개의 반응"
    }
    return emojisStr
}