package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

// TODO: replace with `Array<Emoji>`
class User(
    dto: UserDtoList
) {
    var token: String = dto.email
    // var likedEmojiList: Array<String> = dto.likedEmojiList ?: arrayOf()
    // var createdEmojiList: Array<String> = dto.createdEmojiList ?: arrayOf()
    val name: String = dto.name
}

// after login
//data class UserDto(
//    @SerializedName("token")
//    val token: String,
//
//    @SerializedName("email")
//    val email: String,
//
//    @SerializedName("username")
//    val name: String,
//
//    @SerializedName("liked_emojis")
//    val likedEmojiList: String?,
//
//    @SerializedName("created_emojis")
//    val createdEmojiList: String?
//)

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
    @SerializedName("username") val name: String,
    val email: String,
    val password: String,
)

class LoginUserDto(
    @SerializedName("username") val name: String,
    val password: String
)

val dummyUser = User(UserDtoList(
    //token = "dummy",
    email = "example@exmaple.com",
    name = "1",
    password = "1234",
    likedEmojiList = null,
    createdEmojiList = null
))