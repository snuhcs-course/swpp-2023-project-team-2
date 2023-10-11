package com.goliath.emojihub.springboot.model

import com.google.cloud.firestore.annotation.PropertyName

data class User (
    var _id: String? = null,
    var email: String? = null,
    var name: String? = null,
    @get:PropertyName("created_emojis")
    var createdEmojis: Array<String>? = null,
    @get:PropertyName("liked_emojis")
    var likedEmojis: Array<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (_id != other._id) return false
        if (name != other.name) return false
        if (!email.contentEquals(other.email)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        return result
    }
}
