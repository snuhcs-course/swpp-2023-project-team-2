package com.goliath.emojihub.springboot.dto.emoji

class PostEmojiRequest (
    var created_by: String = "",
    var video_content: String = "",
    var emoji_unicode: String = "",
    var emoji_label: String = "",
)