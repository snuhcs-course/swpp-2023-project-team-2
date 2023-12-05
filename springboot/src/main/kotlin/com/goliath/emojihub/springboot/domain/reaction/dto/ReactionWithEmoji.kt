package com.goliath.emojihub.springboot.domain.reaction.dto

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto

data class ReactionWithEmoji(
    var id: String = "",
    var created_by: String = "",
    var post_id: String = "",
    var emoji_id: String = "",
    var created_at: String = "",
    // reactionDto 안에 emojiDto 만 추가
    var emojiDto: EmojiDto? = null
) {
    constructor(reactionDto: ReactionDto, emojiDto: EmojiDto?): this() {
        id = reactionDto.id
        created_by = reactionDto.created_by
        post_id = reactionDto.post_id
        emoji_id = reactionDto.emoji_id
        created_at = reactionDto.created_at
        this.emojiDto = emojiDto
    }
}
