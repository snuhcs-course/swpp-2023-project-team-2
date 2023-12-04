package com.goliath.emojihub.springboot.domain.post.dto

import kotlin.streams.asSequence

data class PostDto(
    var id: String = "",
    var created_by: String = "",
    var content: String = "",
    var created_at: String = "",
    var modified_at: String = "",
    var reactions: MutableList<ReactionWithEmojiUnicode> = mutableListOf(),
) {
    constructor(username: String, content: String, dateTime: String) : this() {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val outputStrLength: Long = 20
        id = java.util.Random().ints(outputStrLength, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
        created_by = username
        this.content = content
        created_at = dateTime
        modified_at = dateTime
    }
}

data class ReactionWithEmojiUnicode(
    var id: String = "",
    var emoji_unicode: String = ""
)