package com.goliath.emojihub.springboot.domain

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.post.dto.ReactionWithEmojiUnicode
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.user.dto.UserDto

class TestDto// 각 user는 createdEmojiSize 만큼 emoji 생성
// 각 user는 postSize 만큼 post 생성
// 각 user는 다른 user들의 첫번째 created emoji를 save함 (자신의 다음 user부터 순서대로)
// 각 user 'i'는 각 user 'j'(자신 포함)의 첫번째 post에 자신의 이모지(첫번째), 저장한 이모지(첫번째)로 reaction
// userSize 만큼 user 생성
    () {
    var userSize: Int
    var userList: MutableList<UserDto>

    var createdEmojiSize: Int
    var savedEmojiSize: Int
    var emojiList: MutableList<EmojiDto>

    var postSize: Int
    var postList: MutableList<PostDto>

    var reactionSize: Int
    var reactionList: MutableList<ReactionDto>

    init {
        // you can change this size
        this.userSize = 2
        this.createdEmojiSize = 2
        this.postSize = 2
        //////////////////////////////////////
        this.userList = mutableListOf()
        this.savedEmojiSize = userSize - 1
        this.emojiList = mutableListOf()
        this.postList = mutableListOf()
        this.reactionSize = userSize * 2
        this.reactionList = mutableListOf()
        for (i in 0 until userSize) {
            userList.add(
                UserDto(
                    username = "test_username$i",
                    password = "test_password$i",
                    email = "test_email$i",
                )
            )
            // 각 user는 createdEmojiSize 만큼 emoji 생성
            for (j in 0 until createdEmojiSize) {
                val emojiId = "test_emoji${i}_${j}"
                emojiList.add(
                    EmojiDto(
                        created_at = "test_created_at${i}_${j}",
                        created_by = userList[i].username,
                        emoji_label = "test_emoji_label${i}_${j}",
                        emoji_unicode = "test_emoji_unicode${i}_${j}",
                        id = emojiId,
                        num_saved = 0,
                        thumbnail_url = "test_thumbnail_url${i}_${j}",
                        video_url = "test_video_url${i}_${j}",
                    )
                )
                userList[i].created_emojis!!.add(emojiId)
            }
            // 각 user는 postSize 만큼 post 생성
            for (j in 0 until postSize) {
                val postId = "test_post${i}_${j}"
                postList.add(
                    PostDto(
                        content = "test_content${i}_${j}",
                        created_at = "test_created_at${i}_${j}",
                        created_by = userList[i].username,
                        id = postId,
                        modified_at = "test_modified_at${i}_${j}",
                    )
                )
                userList[i].created_posts!!.add(postId)
            }
        }
        for (i in 0 until userSize) {
            for (j in i + 1 until userSize) {
                userList[i].saved_emojis!!.add(emojiList[createdEmojiSize * j].id)
                emojiList[createdEmojiSize * j].num_saved++
            }
            for (j in 0 until i) {
                userList[i].saved_emojis!!.add(emojiList[createdEmojiSize * j].id)
                emojiList[createdEmojiSize * j].num_saved++
            }
        }
        for (i in 0 until userSize) {
            for (j in 0 until userSize) {
                val reactionIdWithCreatedEmoji = "test_reaction${i}_${j}_c"
                val createdEmojiUnicode = emojiList[createdEmojiSize * i].emoji_unicode
                val reactionIdWithSavedEmoji = "test_reaction${i}_${j}_s"
                val savedEmojiUnicode =
                    if (i != userSize - 1)
                        emojiList[createdEmojiSize * (i + 1)].emoji_unicode
                    else
                        emojiList[0].emoji_unicode
                val createdReactionWithEmojiUnicode = ReactionWithEmojiUnicode(
                    id = reactionIdWithCreatedEmoji,
                    emoji_unicode = createdEmojiUnicode
                )
                val savedReactionWithEmojiUnicode = ReactionWithEmojiUnicode(
                    id = reactionIdWithSavedEmoji,
                    emoji_unicode = savedEmojiUnicode
                )
                reactionList.add(
                    ReactionDto(
                        id = reactionIdWithCreatedEmoji,
                        created_by = userList[i].username,
                        post_id = postList[postSize * j].id,
                        emoji_id = userList[i].created_emojis!![0],
                        created_at = "test_created_at${i}_${j}_c"
                    )
                )
                reactionList.add(
                    ReactionDto(
                        id = reactionIdWithSavedEmoji,
                        created_by = userList[i].username,
                        post_id = postList[postSize * j].id,
                        emoji_id = userList[i].saved_emojis!![0],
                        created_at = "test_created_at${i}_${j}_s"
                    )
                )
                postList[postSize * j].reactions.add(createdReactionWithEmojiUnicode)
                postList[postSize * j].reactions.add(savedReactionWithEmojiUnicode)
            }
        }
    }
}