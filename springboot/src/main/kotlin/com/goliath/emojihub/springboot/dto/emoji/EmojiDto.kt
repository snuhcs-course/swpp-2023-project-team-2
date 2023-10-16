package com.goliath.emojihub.springboot.dto.emoji

import com.goliath.emojihub.springboot.util.getDateTimeNow
import kotlin.streams.asSequence

class EmojiDto (
    // TODO: NULL 처리는 어떻게 할까... (각 필드에 대해 값을 받지 못했을 경우)
    var id: String = "",
    var created_by: String = "",
    var video_url: String = "",
    var emoji_unicode: String = "",
    var emoji_label: String = "",
    var created_at: String = ""
){
    constructor(postEmojiRequest: PostEmojiRequest) : this() {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val outputStrLength: Long = 20
        id = java.util.Random().ints(outputStrLength, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
        created_by = postEmojiRequest.created_by
        video_url = postEmojiRequest.video_url
        emoji_unicode = postEmojiRequest.emoji_unicode
        emoji_label = postEmojiRequest.emoji_label
        created_at = getDateTimeNow()
    }
}