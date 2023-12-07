package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

class Reaction(
    dto: ReactionDto
) {
    val id: String = dto.id
    val createdAt: String = dto.createdAt
    val createdBy: String = dto.createdBy
    val emojiId: String = dto.emojiId
    val postId: String = dto.postId
}
data class ReactionMetaDataDto(
    @SerializedName("emoji_unicode_list")
    val unicodeList: List<String>
)

data class ReactionDto(
    val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("emoji_id") val emojiId: String,
    @SerializedName("post_id") val postId: String
)

data class UploadReactionDto(
    @SerializedName("postId") val postId: String,
    @SerializedName("emojiId") val emojiId: String
)

data class ReactionWithEmojiDto(
    val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("emoji_id") val emojiId: String,
    @SerializedName("post_id") val postId: String,
    @SerializedName("emojiDto") val emojiDto: EmojiDto?
)

class ReactionWithEmoji(
    dto: ReactionWithEmojiDto
) {
    val id: String = dto.id
    val createdAt: String = dto.createdAt
    val createdBy: String = dto.createdBy
    val emojiId: String = dto.emojiId
    val postId: String = dto.postId
    val emojiDto: EmojiDto? = dto.emojiDto
}
