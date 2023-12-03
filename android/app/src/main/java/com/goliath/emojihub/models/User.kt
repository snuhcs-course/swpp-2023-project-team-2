package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

class User(
    dto: UserDto
) {
    val name: String = dto.name
}

data class UserDto(
    val name: String
)

// user list: will be deprecated
data class UserDtoList(
    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val name: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("liked_emojis")
    val likedEmojiList: String?,

    @SerializedName("created_emojis")
    val createdEmojiList: String?
)

class RegisterUserDto(
    @SerializedName("email") val email: String,
    @SerializedName("username") val name: String,
    @SerializedName("password") val password: String,
)

class LoginUserDto(
    @SerializedName("username") val name: String,
    @SerializedName("password") val password: String
)