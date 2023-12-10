package com.goliath.emojihub.springboot.domain.reaction.service

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionWithEmoji
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.INDEX_OUT_OF_BOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.COUNT_OUT_OF_BOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.POST_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.EMOJI_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.REACTION_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_ALREADY_REACT
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.REACTION_DELETE_FORBIDDEN
import com.goliath.emojihub.springboot.global.util.StringValue.ReactionField.POST_ID
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
        private val testDto = TestDto()
    }

    @Test
    @DisplayName("게시글의 리액션 가져오기")
    fun getReactionsOfPost() {
        // given
        val post = testDto.postList[0]
        val postId = post.id
        val index = 1
        val wrongIndex = 0
        val count = 10
        val wrongCount = 0
        val emojiUnicodeAll = ""
        val emojiUnicodeSpecific = if (post.reactions.size != 0) post.reactions[0].emoji_unicode else ""
        val wrongId = "wrong_post_id"
        val reactionsAll = mutableListOf<ReactionDto>()
        var reactionsSpecific = mutableListOf<ReactionDto>()
        Mockito.`when`(postDao.getPost(postId)).thenReturn(post)
        Mockito.`when`(postDao.getPost(wrongId)).thenReturn(null)
        for (reactionWithEmojiUnicode in post.reactions) {
            if (reactionWithEmojiUnicode.emoji_unicode != emojiUnicodeSpecific) continue
            for (reaction in testDto.reactionList) {
                if (reaction.id != reactionWithEmojiUnicode.id) continue
                reactionsSpecific.add(reaction)
            }
        }
        for (reaction in testDto.reactionList) {
            if (reaction.post_id == postId) {
                reactionsAll.add(reaction)
            }
        }
        if (reactionsSpecific.size != 0) {
            reactionsSpecific.sortByDescending { it.created_at }
            reactionsSpecific = reactionsSpecific.subList(
                Integer.min((index - 1) * count, reactionsSpecific.size - 1),
                Integer.min(index * count, reactionsSpecific.size)
            )
        }
        val reactionWithEmojiAllList = mutableListOf<ReactionWithEmoji>()
        val reactionWithEmojiSpecificList = mutableListOf<ReactionWithEmoji>()
        for (reaction in reactionsAll) {
            for (emoji in testDto.emojiList) {
                if (emoji.id != reaction.emoji_id) continue
                reactionWithEmojiAllList.add(
                    ReactionWithEmoji(reaction, emoji)
                )
            }
        }
        for (reaction in reactionsSpecific) {
            for (emoji in testDto.emojiList) {
                if (emoji.id != reaction.emoji_id) continue
                reactionWithEmojiSpecificList.add(
                    ReactionWithEmoji(reaction, emoji)
                )
            }
        }
        Mockito.`when`(reactionDao.getReactionsWithField(postId, POST_ID.string)).thenReturn(reactionsAll)
        for (reaction in reactionsSpecific) {
            Mockito.`when`(reactionDao.getReaction(reaction.id)).thenReturn(reaction)
        }
        for (emoji in testDto.emojiList) {
            Mockito.`when`(emojiDao.getEmoji(emoji.id)).thenReturn(emoji)
        }

        // when
        val result1 = reactionService.getReactionsOfPost(postId, emojiUnicodeAll, index, count)
        val result2 = reactionService.getReactionsOfPost(postId, emojiUnicodeSpecific, index, count)
        val assertThrows1 = assertThrows(CustomHttp400::class.java) {
            reactionService.getReactionsOfPost(postId, emojiUnicodeAll, wrongIndex, count)
        }
        val assertThrows2 = assertThrows(CustomHttp400::class.java) {
            reactionService.getReactionsOfPost(postId, emojiUnicodeAll, index, wrongCount)
        }
        val assertThrows3 = assertThrows(CustomHttp404::class.java) {
            reactionService.getReactionsOfPost(wrongId, emojiUnicodeAll, index, count)
        }

        // then
        assertAll(
            { assertEquals(result1, reactionWithEmojiAllList) },
            { assertEquals(result2, reactionWithEmojiSpecificList) },
            { assertEquals(assertThrows1.message, INDEX_OUT_OF_BOUND.getMessage()) },
            { assertEquals(assertThrows2.message, COUNT_OUT_OF_BOUND.getMessage()) },
            { assertEquals(assertThrows3.message, POST_NOT_FOUND.getMessage()) },
        )
        verify(postDao, times(2)).getPost(postId)
        verify(postDao, times(1)).getPost(wrongId)
        verify(reactionDao, times(1)).getReactionsWithField(postId, POST_ID.string)
        for (reaction in reactionsSpecific) {
            verify(reactionDao, times(1)).getReaction(reaction.id)
        }
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
            { assertEquals(assertThrows1.message, USER_NOT_FOUND.getMessage()) },
            { assertEquals(assertThrows2.message, POST_NOT_FOUND.getMessage()) },
            { assertEquals(assertThrows3.message, EMOJI_NOT_FOUND.getMessage()) },
            { assertEquals(assertThrows4.message, USER_ALREADY_REACT.getMessage()) }
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
            { assertEquals(assertThrows1.message, REACTION_NOT_FOUND.getMessage()) },
            { assertEquals(assertThrows2.message, REACTION_DELETE_FORBIDDEN.getMessage()) }
        )
        verify(reactionDao, times(2)).getReaction(reactionId)
        verify(reactionDao, times(1)).getReaction(wrongReactionId)
        verify(postDao, times(1)).deleteReaction(reaction.post_id, reactionId)
        verify(reactionDao, times(1)).deleteReaction(reactionId)
    }
}