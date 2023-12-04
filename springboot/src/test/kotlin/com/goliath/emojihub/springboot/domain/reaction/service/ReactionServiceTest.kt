package com.goliath.emojihub.springboot.domain.reaction.service

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(ReactionService::class)
internal class ReactionServiceTest {

    @Autowired
    lateinit var reactionService: ReactionService

    @MockBean
    lateinit var postDao: PostDao

    @MockBean
    lateinit var userDao: UserDao

    @MockBean
    lateinit var reactionDao: ReactionDao

    @MockBean
    lateinit var emojiDao: EmojiDao

    companion object {
        const val POST_ID = "post_id"
        private val testDto = TestDto()
    }

    @Test
    @DisplayName("게시글의 리액션 가져오기")
    fun getReactionsOfPost() {
        // given
        val postId = testDto.postList[0].id
        val wrongId = "wrong_post_id"
        val reactions = mutableListOf<ReactionDto>()
        Mockito.`when`(postDao.existPost(postId)).thenReturn(true)
        Mockito.`when`(postDao.existPost(wrongId)).thenReturn(false)
        for (reaction in testDto.reactionList) {
            if (reaction.post_id == postId) {
                reactions.add(reaction)
            }
        }
        Mockito.`when`(reactionDao.getReactionsWithField(postId, POST_ID)).thenReturn(reactions)

        // when
        val result = reactionService.getReactionsOfPost(postId)
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            reactionService.getReactionsOfPost(wrongId)
        }

        // then
        assertAll(
            { assertEquals(result, reactions) },
            { assertEquals(assertThrows.message, "Post doesn't exist.") }
        )
        verify(postDao, times(1)).existPost(postId)
        verify(postDao, times(1)).existPost(wrongId)
        verify(reactionDao, times(1)).getReactionsWithField(postId, POST_ID)
    }

    @Test
    @DisplayName("리액션 올리기")
    fun postReaction() {
        // given
        val reaction = testDto.reactionList[0]
        val username = reaction.created_by
        val postId = reaction.post_id
        val emojiId = reaction.emoji_id
        val emoji = testDto.emojiList[0]
        val wrongUsername = "wrong_username"
        val wrongPostId = "wrong_post_id"
        val wrongEmojiId = "wrong_emoji_id"
        val sameEmojiId = "same_emoji_id"
        Mockito.`when`(userDao.existUser(username)).thenReturn(true)
        Mockito.`when`(userDao.existUser(wrongUsername)).thenReturn(false)
        Mockito.`when`(postDao.existPost(postId)).thenReturn(true)
        Mockito.`when`(postDao.existPost(wrongPostId)).thenReturn(false)
        Mockito.`when`(emojiDao.getEmoji(emojiId)).thenReturn(emoji)
        Mockito.`when`(emojiDao.getEmoji(sameEmojiId)).thenReturn(emoji)
        Mockito.`when`(emojiDao.getEmoji(wrongEmojiId)).thenReturn(null)
        Mockito.`when`(reactionDao.existSameReaction(username, postId, emojiId)).thenReturn(false)
        Mockito.`when`(reactionDao.existSameReaction(username, postId, sameEmojiId)).thenReturn(true)
        Mockito.`when`(reactionDao.insertReaction(username, postId, emojiId)).thenReturn(reaction)

        // when
        reactionService.postReaction(username, postId, emojiId)
        val assertThrows1 = assertThrows(CustomHttp404::class.java) {
            reactionService.postReaction(wrongUsername, postId, emojiId)
        }
        val assertThrows2 = assertThrows(CustomHttp404::class.java) {
            reactionService.postReaction(username, wrongPostId, emojiId)
        }
        val assertThrows3 = assertThrows(CustomHttp404::class.java) {
            reactionService.postReaction(username, postId, wrongEmojiId)
        }
        val assertThrows4 = assertThrows(CustomHttp403::class.java) {
            reactionService.postReaction(username, postId, sameEmojiId)
        }

        // then
        assertAll(
            { assertEquals(assertThrows1.message, "User doesn't exist.") },
            { assertEquals(assertThrows2.message, "Post doesn't exist.") },
            { assertEquals(assertThrows3.message, "Emoji doesn't exist.") },
            { assertEquals(assertThrows4.message, "User already react to this post with this emoji.") }
        )
        verify(userDao, times(4)).existUser(username)
        verify(userDao, times(1)).existUser(wrongUsername)
        verify(postDao, times(3)).existPost(postId)
        verify(postDao, times(1)).existPost(wrongPostId)
        verify(emojiDao, times(1)).getEmoji(emojiId)
        verify(emojiDao, times(1)).getEmoji(wrongEmojiId)
        verify(emojiDao, times(1)).getEmoji(sameEmojiId)
        verify(reactionDao, times(1)).existSameReaction(username, postId, emojiId)
        verify(reactionDao, times(1)).existSameReaction(username, postId, sameEmojiId)
        verify(reactionDao, times(1)).insertReaction(username, postId, emojiId)
        verify(postDao, times(1)).insertReaction(postId, reaction.id, emoji.emoji_unicode)
    }

    @Test
    @DisplayName("리액션 삭제하기")
    fun deleteReaction() {
        // given
        val reaction = testDto.reactionList[0]
        val reactionId = reaction.id
        val username = reaction.created_by
        val wrongReactionId = "wrong_reaction_id"
        val wrongUsername = "wrong_username"
        Mockito.`when`(reactionDao.getReaction(reactionId)).thenReturn(reaction)
        Mockito.`when`(reactionDao.getReaction(wrongReactionId)).thenReturn(null)

        // when
        reactionService.deleteReaction(username, reactionId)
        val assertThrows1 = assertThrows(CustomHttp404::class.java) {
            reactionService.deleteReaction(username, wrongReactionId)
        }
        val assertThrows2 = assertThrows(CustomHttp403::class.java) {
            reactionService.deleteReaction(wrongUsername, reactionId)
        }

        // then
        assertAll(
            { assertEquals(assertThrows1.message, "Reaction doesn't exist.") },
            { assertEquals(assertThrows2.message, "You can't delete this reaction.") }
        )
        verify(reactionDao, times(2)).getReaction(reactionId)
        verify(reactionDao, times(1)).getReaction(wrongReactionId)
        verify(postDao, times(1)).deleteReaction(reaction.post_id, reactionId)
        verify(reactionDao, times(1)).deleteReaction(reactionId)
    }
}