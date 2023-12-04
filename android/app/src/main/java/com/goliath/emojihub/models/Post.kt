package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

class Post(
    dto: PostDto
) {
    val id: String = dto.id
    val createdAt: String = dto.createdAt
    val modifiedAt: String = dto.modifiedAt
    val createdBy: String = dto.createdBy
    val content: String = dto.content
    val reaction: List<ReactionWithEmojiUnicode> = dto.reaction

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post
        if (id != other.id) return false
        if (createdAt != other.createdAt) return false
        if (modifiedAt != other.modifiedAt) return false
        if (createdBy != other.createdBy) return false
        if (content != other.content) return false
        if (reaction != other.reaction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + modifiedAt.hashCode()
        result = 31 * result + createdBy.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + reaction.hashCode()
        return result
    }
}

data class PostDto(
    val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("modified_at") val modifiedAt: String,
    @SerializedName("created_by") val createdBy: String,
    val content: String,
    @SerializedName("reactions") val reaction: List<ReactionWithEmojiUnicode>
)

data class UploadPostDto(
    @SerializedName("content") val content: String
)

data class ReactionWithEmojiUnicode(
    var id: String = "",
    var emoji_unicode: String = ""
)