package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

data class ReactionMetaDataDto(
    @SerializedName("emoji_unicode_list")
    val unicodeList: List<String>
)

data class ReactionDto(
    @SerializedName("emoji_list")
    val emojiList: List<EmojiMetaDataDto>
)