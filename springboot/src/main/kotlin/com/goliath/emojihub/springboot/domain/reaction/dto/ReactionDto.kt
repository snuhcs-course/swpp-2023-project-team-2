package com.goliath.emojihub.springboot.domain.reaction.dto


data class ReactionDto(
    var id: String = "",
    var created_by: String = "",
    var post_id: String = "",
    var emoji_id: String = "",
    var created_at: String = ""
)

class ReactionDtoBuilder {
    private val reactionDto: ReactionDto = ReactionDto()

    fun id(id: String): ReactionDtoBuilder {
        reactionDto.id = id
        return this
    }

    fun createdBy(createdBy: String): ReactionDtoBuilder {
        reactionDto.created_by = createdBy
        return this
    }

    fun postId(postId: String): ReactionDtoBuilder {
        reactionDto.post_id = postId
        return this
    }

    fun emojiId(emojiId: String): ReactionDtoBuilder {
        reactionDto.emoji_id = emojiId
        return this
    }

    fun createdAt(createdAt: String): ReactionDtoBuilder {
        reactionDto.created_at = createdAt
        return this
    }

    fun build(): ReactionDto {
        return reactionDto
    }
}