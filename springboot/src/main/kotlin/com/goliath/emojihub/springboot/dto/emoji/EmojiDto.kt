package com.goliath.emojihub.springboot.dto.emoji

import com.goliath.emojihub.springboot.util.getNow

class EmojiDto (
    var id: String,
    var created_by: String? = null,
    var video_url: String? = null,
    var emoji_unicode: String? = null,
    var emoji_label: String? = null,
    var created_at: String? = null
){
    //TODO: 이 부분은 좀 더 고민해봐야 할 것 같다.
}