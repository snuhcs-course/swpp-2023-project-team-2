package com.goliath.emojihub.springboot.domain.emoji.dto

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
    constructor(username: String, postEmojiRequest: PostEmojiRequest, emojiVideoUrl: String, dateTime: String) : this() {
        // TODO: 이 constructor도 완전 의식의 흐름대로 만들었다... 수정 필요
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val outputStrLength: Long = 20
        id = java.util.Random().ints(outputStrLength, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")

        created_by = username
        video_url = emojiVideoUrl
        emoji_unicode = postEmojiRequest.emoji_unicode
        emoji_label = postEmojiRequest.emoji_label
        created_at = dateTime
    }
}