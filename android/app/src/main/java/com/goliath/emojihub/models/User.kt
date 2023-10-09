package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

// TODO: replace with `Array<Emoji>`
class User(
    dto: UserDto
) {
    var likedEmojiList: Array<String> = dto.likedEmojiList
    var createdEmojiList: Array<String> = dto.createdEmojiList
    val userId: String = dto.userId
}

class UserDto(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("liked_emoji_list")
    val likedEmojiList: Array<String>,

    @SerializedName("created_emoji_list")
    val createdEmojiList: Array<String>
)