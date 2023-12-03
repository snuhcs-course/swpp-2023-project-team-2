package com.goliath.emojihub.springboot.domain.user.dto

data class UserDto (
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var created_emojis: MutableList<String>? = mutableListOf(),
    var saved_emojis: MutableList<String>? = mutableListOf(),
    var created_posts: MutableList<String>? = mutableListOf(),
) {
    data class AuthToken(
        val accessToken: String,
    )
}
