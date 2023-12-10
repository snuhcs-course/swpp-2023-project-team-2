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
    val savedEmojiList: List<String> = dto.savedEmojiList
    val createdEmojiList: List<String> = dto.createdEmojiList
    val createdPostList: List<String> = dto.createdPostList

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDetails

        if (name != other.name) return false
        if (email != other.email) return false
        if (savedEmojiList != other.savedEmojiList) return false
        if (createdEmojiList != other.createdEmojiList) return false
        return createdPostList == other.createdPostList
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + savedEmojiList.hashCode()
        result = 31 * result + createdEmojiList.hashCode()
        result = 31 * result + createdPostList.hashCode()
        return result
    }
}

data class UserDetailsDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val name: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("saved_emojis")
    val savedEmojiList: List<String>,

    @SerializedName("created_emojis")
    val createdEmojiList: List<String>,

    @SerializedName("created_posts")
    val createdPostList: List<String>
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