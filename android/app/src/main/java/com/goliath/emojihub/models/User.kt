package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

// TODO: replace with `Array<Emoji>`
class User(
    dto: UserDto
) {
    var token: String = dto.token
    var likedEmojiList: Array<String> = dto.likedEmojiList
    var createdEmojiList: Array<String> = dto.createdEmojiList
    val userId: String = dto.userId
}

class UserDto(
    @SerializedName("token")
    val token: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("liked_emoji_list")
    val likedEmojiList: Array<String>,

    @SerializedName("created_emoji_list")
    val createdEmojiList: Array<String>
)

// TODO: refactor directory if needed
data class UserResponse(
    @SerializedName("access_token") val token: String
)

val dummyUser = User(UserDto(
    token = "dummy",
    userId = "1",
    likedEmojiList = arrayOf(),
    createdEmojiList = arrayOf()
))