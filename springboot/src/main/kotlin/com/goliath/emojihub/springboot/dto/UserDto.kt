package com.goliath.emojihub.springboot.dto

data class UserDto (
    var id: String? = null,
    var email: String? = null,
    var username: String? = null,
    var password: String? = null,
    var created_emojis: MutableList<String>? = null,
    var liked_emojis: MutableList<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDto

        if (id != other.id) return false
        if (username != other.username) return false
        if (!email.contentEquals(other.email)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        return result
    }

    constructor(signUpRequest: SignUpRequest) : this() {
        id = signUpRequest.id
        email = signUpRequest.email
        username = signUpRequest.username
        password = signUpRequest.password
    }
}
