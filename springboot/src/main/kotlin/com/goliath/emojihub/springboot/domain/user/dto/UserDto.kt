package com.goliath.emojihub.springboot.domain.user.dto

data class UserDto (
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var created_emojis: MutableList<String>? = mutableListOf(),
    var liked_emojis: MutableList<String>? = mutableListOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDto

        if (username != other.username) return false
        if (!email.contentEquals(other.email)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        return result
    }

    constructor(signUpRequest: SignUpRequest) : this() {
        email = signUpRequest.email
        username = signUpRequest.username
        password = signUpRequest.password
    }

    data class AuthToken(
        val accessToken: String,
    )
}
