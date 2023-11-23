package com.goliath.emojihub.springboot.domain.emoji.dto

import kotlin.streams.asSequence

data class EmojiDto(
    var id: String = "",
    var created_by: String = "",
    var video_url: String = "",
    var emoji_unicode: String = "",
    var emoji_label: String = "",
    var created_at: String = "",
    var num_saved: Int = 0,
    var thumbnail_url: String = ""
){
    constructor(username: String, emojiUnicode: String, emojiLabel: String, emojiVideoUrl: String, dateTime: String, thumbnailUrl: String) : this() {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val outputStrLength: Long = 20
        id = java.util.Random().ints(outputStrLength, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")

        created_by = username
        video_url = emojiVideoUrl
        emoji_unicode = emojiUnicode
        emoji_label = emojiLabel
        created_at = dateTime
        num_saved = 0
        thumbnail_url = thumbnailUrl
    }
}