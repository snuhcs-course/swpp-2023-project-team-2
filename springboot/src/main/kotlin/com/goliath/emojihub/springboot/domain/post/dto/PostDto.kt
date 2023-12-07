package com.goliath.emojihub.springboot.domain.post.dto

data class PostDto(
    var id: String = "",
    var created_by: String = "",
    var content: String = "",
    var created_at: String = "",
    var modified_at: String = "",
    var reactions: MutableList<ReactionWithEmojiUnicode> = mutableListOf(),
)

data class ReactionWithEmojiUnicode(
    var id: String = "",
    var emoji_unicode: String = ""
)

class PostDtoBuilder {
    private val postDto: PostDto = PostDto()

    fun id(id: String): PostDtoBuilder {
        postDto.id = id
        return this
    }

    fun createdBy(createdBy: String): PostDtoBuilder {
        postDto.created_by = createdBy
        return this
    }

    fun content(content: String): PostDtoBuilder {
        postDto.content = content
        return this
    }

    fun createdAt(createdAt: String): PostDtoBuilder {
        postDto.created_at = createdAt
        return this
    }

    fun modifiedAt(modifiedAt: String): PostDtoBuilder {
        postDto.modified_at = modifiedAt
        return this
    }

    fun reactions(reactions: MutableList<ReactionWithEmojiUnicode>): PostDtoBuilder {
        postDto.reactions = reactions
        return this
    }

    fun build(): PostDto {
        return postDto
    }
}