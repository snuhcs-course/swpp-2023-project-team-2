package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName

class Emoji(
    dto: EmojiDto
) {
    val createdBy: String = dto.createdBy
    var isSaved: Boolean = false
    val createdAt: String = dto.createdAt
    val savedCount: Int = dto.savedCount
    val videoLink: String = dto.videoLink
    val unicode: String = dto.unicode
    val label: String = dto.label
    val id: String = dto.id
}

data class EmojiDto(
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("num_saved") val savedCount: Int,
    @SerializedName("video_url") val videoLink: String,
    @SerializedName("id") val id: String,
    @SerializedName("emoji_label") val label: String,
    @SerializedName("emoji_unicode") val unicode: String
)

data class EmojiMetaDataDto(
    @SerializedName("emoji") val emoji: String,
    @SerializedName("used_by") val usedBy: String,
    @SerializedName("used_at") val usedAt: String
)

data class UploadEmojiDto(
    @SerializedName("emoji_unicode") val emojiUnicode: String,
    @SerializedName("emoji_label") val emojiLabel: String
)

data class FetchEmojiListDto(
    @SerializedName("sortByDate") val sortByDate: Int,
    @SerializedName("index") val index: Int,
    @SerializedName("count") val count: Int
)

val dummyEmoji = Emoji(
    EmojiDto(
        createdBy = "channn",
        createdAt = "2023.09.16",
        savedCount = 1600,
        videoLink = "https://firebasestorage.googleapis.com/v0/b/emojihub-e2023.appspot.com/o/sample_videos%2Fthumbs%20up.mp4?alt=media&token=9526818f-6ccb-499f-84b2-e6e1f6924704",
        unicode = "U+1F44D",
        id = "1234",
        label = "sample"
    )
)

val dummyUsernames = listOf("channn", "doggydog", "meow_0w0", "mpunchmm", "kick_back")
val dummyUnicodes = listOf("U+1F44D", "U+1F600", "U+1F970", "U+1F60E", "U+1F621", "U+1F63A", "U+1F496", "U+1F415")
val dummySavedCounts = 0..2000

fun createDummyEmoji(): Emoji {
    return Emoji(
        EmojiDto(
            createdBy = dummyUsernames.random(),
            createdAt = "2023.09.16",
            savedCount = dummySavedCounts.random(),
            videoLink = "",
            unicode = dummyUnicodes.random(),
            id = dummySavedCounts.random().toString(),
            label = "cat"
        )
    )
}