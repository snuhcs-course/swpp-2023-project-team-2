package com.goliath.emojihub.springboot.domain.emoji.dto

class PostEmojiRequest (
    var created_by: String = "",
    var emoji_unicode: String = "",
    var emoji_label: String = "",
)