package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

class Emoji(
    dto: EmojiDto
) {
    val createdBy: String = dto.createdBy
    val createdAt: String = dto.createdAt
    val savedCount: Int = dto.savedCount
    val videoLink: String = dto.videoLink
    val unicode: String = dto.unicode
}

data class EmojiDto(
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("saved_count") val savedCount: Int,
    @SerializedName("video") val videoLink: String,
    val unicode: String
)

data class EmojiMetaDataDto(
    @SerializedName("emoji") val emoji: String,
    @SerializedName("used_by") val usedBy: String,
    @SerializedName("used_at") val usedAt: String
)