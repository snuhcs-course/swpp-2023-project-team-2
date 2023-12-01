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
    val thumbnailLink: String = dto.thumbnailLink
    val unicode: String = dto.unicode
    val label: String = dto.label
    val id: String = dto.id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Emoji
        if (createdBy != other.createdBy) return false
        if (isSaved != other.isSaved) return false
        if (createdAt != other.createdAt) return false
        if (savedCount != other.savedCount) return false
        if (videoLink != other.videoLink) return false
        if (thumbnailLink != other.thumbnailLink) return false
        if (unicode != other.unicode) return false
        if (label != other.label) return false
        if (id != other.id) return false

        return true
    }
    override fun hashCode(): Int {
        var result = createdBy.hashCode()
        result = 31 * result + isSaved.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + savedCount
        result = 31 * result + videoLink.hashCode()
        result = 31 * result + thumbnailLink.hashCode()
        result = 31 * result + unicode.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}

data class EmojiDto(
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("num_saved") val savedCount: Int,
    @SerializedName("video_url") val videoLink: String,
    @SerializedName("thumbnail_url") val thumbnailLink: String,
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
        thumbnailLink = "https://storage.googleapis.com/emojihub-e2023.appspot.com/username1_2023-11-22%2008%3A45%3A29.jpeg?GoogleAccessId=firebase-adminsdk-zynbm@emojihub-e2023.iam.gserviceaccount.com&Expires=1709250330&Signature=Mht0X%2BkLsGGzGKq1yT2MmhUOzW8p9FqWd659Ggb8isEG7UkKvVFOdnqd4U6iPvS7JWv%2FhHyPp%2F%2FnZyQ4%2F6smLSAyQtRCUNtuKkbVVN0bTP8a8Wo5BwMjRlj5rFyyyo3hDAAOZIr3Qj6OThGcvxldGXnYVFtc5qWCLkkb%2FYS5QoHa9NYbqx0Hj10T5QfKSHhayi3%2BXEBgN59nzdrmFl7dsJ4RW8043EvXNF20eNw9DFmiRSIFqh7dT9Q3hd21GVgoIBOlXJ%2BsI%2BWu2vy61NXcfKhlZzAwS8eh8jHSxQZ%2FjPHE3itl7fFfgcFvapcJ9d%2BkFjwqhr7j4D2mcBF0yr1tHQ%3D%3D",
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
            thumbnailLink = "",
            unicode = dummyUnicodes.random(),
            id = dummySavedCounts.random().toString(),
            label = "cat"
        )
    )
}