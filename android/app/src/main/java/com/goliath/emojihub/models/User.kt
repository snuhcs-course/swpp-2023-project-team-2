package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

// TODO: implement User and UserDto
class User(
    dto: UserDto
) {
    var accessToken: String = dto.accessToken
    val name: String = dto.name
}

data class UserDto(
    val accessToken: String,
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