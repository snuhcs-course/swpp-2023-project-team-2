package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import java.io.File

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

data class uploadEmojiDto(
    @SerializedName("emoji_unicode") val emojiUnicode: String,
    @SerializedName("emoji_label") val emojiLabel: String,
    @SerializedName("video_file") val videoFile: File //TODO: is this the correct data type?
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
            unicode = dummyUnicodes.random()
        )
    )
}