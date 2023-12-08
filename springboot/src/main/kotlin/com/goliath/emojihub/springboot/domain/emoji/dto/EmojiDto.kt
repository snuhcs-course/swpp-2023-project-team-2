package com.goliath.emojihub.springboot.domain.emoji.dto

data class EmojiDto(
    var id: String = "",
    var created_by: String = "",
    var video_url: String = "",
    var emoji_unicode: String = "",
    var emoji_label: String = "",
    var created_at: String = "",
    var num_saved: Int = 0,
    var thumbnail_url: String = ""
)

class EmojiDtoBuilder {
    private val emojiDto: EmojiDto = EmojiDto()

    fun id(id: String): EmojiDtoBuilder {
        emojiDto.id = id
        return this
    }

    fun createdBy(createdBy: String): EmojiDtoBuilder {
        emojiDto.created_by = createdBy
        return this
    }

    fun videoUrl(videoUrl: String): EmojiDtoBuilder {
        emojiDto.video_url = videoUrl
        return this
    }

    fun emojiUnicode(emojiUnicode: String): EmojiDtoBuilder {
        emojiDto.emoji_unicode = emojiUnicode
        return this
    }

    fun emojiLabel(emojiLabel: String): EmojiDtoBuilder {
        emojiDto.emoji_label = emojiLabel
        return this
    }

    fun createdAt(createdAt: String): EmojiDtoBuilder {
        emojiDto.created_at = createdAt
        return this
    }

    fun numSaved(numSaved: Int): EmojiDtoBuilder {
        emojiDto.num_saved = numSaved
        return this
    }

    fun thumbnailUrl(thumbnailUrl: String): EmojiDtoBuilder {
        emojiDto.thumbnail_url = thumbnailUrl
        return this
    }

    fun build(): EmojiDto {
        return emojiDto
    }
}