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

class UserDetails(
    dto: UserDetailsDto
) {
    val name: String = dto.name
    val email: String = dto.email
    val savedEmojiList: List<String>? = dto.savedEmojiList
    val createdEmojiList: List<String>? = dto.createdEmojiList
    val createdPostList: List<String>? = dto.createdPostList
}

data class UserDetailsDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val name: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("liked_emojis")
    val savedEmojiList: List<String>?,

    @SerializedName("created_emojis")
    val createdEmojiList: List<String>?,

    @SerializedName("created_posts")
    val createdPostList: List<String>?
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