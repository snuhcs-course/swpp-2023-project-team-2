package com.goliath.emojihub.springboot.domain.reaction.dto

import kotlin.streams.asSequence

data class ReactionDto(
    var id: String = "",
    var created_by: String = "",
    var post_id: String = "",
    var emoji_id: String = "",
    var created_at: String = ""
) {
    constructor(username: String, post_id: String, emoji_id: String, dateTime: String) : this() {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val outputStrLength: Long = 20
        id = java.util.Random().ints(outputStrLength, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
        created_by = username
        this.post_id = post_id
        this.emoji_id = emoji_id
        created_at = dateTime
    }
}