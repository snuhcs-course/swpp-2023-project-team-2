package com.goliath.emojihub.springboot.domain.user.dto

data class UserDto (
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var created_emojis: MutableList<String> = mutableListOf(),
    var saved_emojis: MutableList<String> = mutableListOf(),
    var created_posts: MutableList<String> = mutableListOf(),
) {
    data class AuthToken(
        val accessToken: String,
    )
}

class UserDtoBuilder {
    private val userDto: UserDto = UserDto()

    fun email(email: String): UserDtoBuilder {
        userDto.email = email
        return this
    }

    fun username(username: String): UserDtoBuilder {
        userDto.username = username
        return this
    }

    fun password(password: String): UserDtoBuilder {
        userDto.password = password
        return this
    }

    fun build(): UserDto {
        return userDto
    }
}