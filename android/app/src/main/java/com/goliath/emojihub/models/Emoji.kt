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

val dummyEmoji = Emoji(
    EmojiDto(
        createdBy = "channn",
        createdAt = "2023.09.16",
        savedCount = 1600,
        videoLink = "https://firebasestorage.googleapis.com/v0/b/emojihub-e2023.appspot.com/o/sample_videos%2Fthumbs%20up.mp4?alt=media&token=9526818f-6ccb-499f-84b2-e6e1f6924704",
        unicode = "U+1F44D"

    )
)