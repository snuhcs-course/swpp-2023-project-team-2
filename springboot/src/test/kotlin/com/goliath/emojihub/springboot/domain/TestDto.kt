package com.goliath.emojihub.springboot.domain

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDtoBuilder
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.post.dto.PostDtoBuilder
import com.goliath.emojihub.springboot.domain.post.dto.ReactionWithEmojiUnicode
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDtoBuilder
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.domain.user.dto.UserDtoBuilder

class TestDto// 각 user는 createdEmojiSize 만큼 emoji 생성
// 각 user는 postSize 만큼 post 생성
// 각 user는 다른 user들의 첫번째 created emoji를 save함 (자신의 다음 user부터 순서대로)
// 각 user 'i'는 각 user 'j'(자신 포함)의 첫번째 post에 자신의 이모지(첫번째), 저장한 이모지(첫번째)로 reaction
// userSize 만큼 user 생성
{
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
                UserDtoBuilder()
                    .username("test_username$i")
                    .password("test_password$i")
                    .email("test_email$i")
                    .build()
            )
            // 각 user는 createdEmojiSize 만큼 emoji 생성
            for (j in 0 until createdEmojiSize) {
                val emojiId = "test_emoji${i}_${j}"
                emojiList.add(
                    EmojiDtoBuilder()
                    .createdAt("test_created_at${i}_${j}")
                    .createdBy(userList[i].username)
                    .emojiLabel("test_emoji_label${i}_${j}")
                    .emojiUnicode("test_emoji_unicode${i}_${j}")
                    .id(emojiId)
                    .numSaved(0)
                    .thumbnailUrl("test_thumbnail_url${i}_${j}")
                    .videoUrl("test_video_url${i}_${j}")
                    .build()
                )
                userList[i].created_emojis.add(emojiId)
            }
            // 각 user는 postSize 만큼 post 생성
            for (j in 0 until postSize) {
                val postId = "test_post${i}_${j}"
                postList.add(
                    PostDtoBuilder()
                        .content("test_content${i}_${j}")
                        .createdAt("test_created_at${i}_${j}")
                        .createdBy(userList[i].username)
                        .id(postId)
                        .modifiedAt("test_modified_at${i}_${j}")
                        .build()
                )
                userList[i].created_posts.add(postId)
            }
        }
        for (i in 0 until userSize) {
            for (j in i + 1 until userSize) {
                userList[i].saved_emojis.add(emojiList[createdEmojiSize * j].id)
                emojiList[createdEmojiSize * j].num_saved++
            }
            for (j in 0 until i) {
                userList[i].saved_emojis.add(emojiList[createdEmojiSize * j].id)
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
                    ReactionDtoBuilder()
                        .id(reactionIdWithCreatedEmoji)
                        .createdBy(userList[i].username)
                        .postId(postList[postSize * j].id)
                        .emojiId(userList[i].created_emojis[0])
                        .createdAt("test_created_at${i}_${j}_c")
                        .build()
                )
                reactionList.add(
                    ReactionDtoBuilder()
                        .id(reactionIdWithSavedEmoji)
                        .createdBy(userList[i].username)
                        .postId(postList[postSize * j].id)
                        .emojiId(userList[i].saved_emojis[0])
                        .createdAt("test_created_at${i}_${j}_s")
                        .build()
                )
                postList[postSize * j].reactions.add(createdReactionWithEmojiUnicode)
                postList[postSize * j].reactions.add(savedReactionWithEmojiUnicode)
            }
        }
    }
}