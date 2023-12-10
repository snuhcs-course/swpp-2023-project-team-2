package com.goliath.emojihub.springboot.domain.emoji.service

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.INDEX_OUT_OF_BOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.COUNT_OUT_OF_BOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.EMOJI_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_CREATED
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_ALREADY_SAVED
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_ALREADY_UNSAVED
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.EMOJI_DELETE_FORBIDDEN
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.CREATED_EMOJIS
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.SAVED_EMOJIS
import com.goliath.emojihub.springboot.global.util.StringValue.ReactionField.EMOJI_ID
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(EmojiService::class)
internal class EmojiServiceTest {

    @Autowired
    lateinit var emojiService: EmojiService

    @MockBean
    lateinit var emojiDao: EmojiDao

    @MockBean
    lateinit var userDao: UserDao

    @MockBean
    lateinit var reactionDao: ReactionDao

    @MockBean
    lateinit var postDao: PostDao

    companion object {
        private val testDto = TestDto()
    }

    @Test
    @DisplayName("이모지 데이터 가져오기")
    fun getEmojis() {
        // given
        val sortByDate = 0
        val index = 1
        val count = 10
        val wrongIndex = 0
        val wrongCount = 0
        Mockito.`when`(emojiDao.getEmojis(sortByDate, index, count)).thenReturn(testDto.emojiList)

        // when
        val result = emojiService.getEmojis(sortByDate, index, count)
        val assertThrows1 = assertThrows(CustomHttp400::class.java) {
            emojiService.getEmojis(sortByDate, wrongIndex, count)
        }
        val assertThrows2 = assertThrows(CustomHttp400::class.java) {
            emojiService.getEmojis(sortByDate, index, wrongCount)
        }

        // then
        assertAll(
            { assertEquals(result, testDto.emojiList) },
            { assertEquals(assertThrows1.message, INDEX_OUT_OF_BOUND.getMessage()) },
            { assertEquals(assertThrows2.message, COUNT_OUT_OF_BOUND.getMessage()) }
        )
        verify(emojiDao, times(1)).getEmojis(sortByDate, index, count)
    }

    @Test
    @DisplayName("자신의 이모지 데이터 가져오기")
    fun getMyEmojis() {
        // given
        val user = testDto.userList[0]
        val username = user.username
        val wrongUsername = "wrong_username"
        val index = 1
        val countCreated = testDto.createdEmojiSize
        val countSaved = testDto.savedEmojiSize
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)
        Mockito.`when`(userDao.getUser(wrongUsername)).thenReturn(null)
        for (emoji in testDto.emojiList) {
            Mockito.`when`(emojiDao.getEmoji(emoji.id)).thenReturn(emoji)
        }

        // when
        val createdEmojisResult = emojiService.getMyEmojis(username, CREATED_EMOJIS.string, index, countCreated)
        val savedEmojisResult = emojiService.getMyEmojis(username, SAVED_EMOJIS.string, index, countSaved)
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            emojiService.getMyEmojis(wrongUsername, CREATED_EMOJIS.string, index, countCreated)
        }

        // then
        assertAll(
            { assertEquals(createdEmojisResult.size, testDto.createdEmojiSize) },
            { assertEquals(savedEmojisResult.size, testDto.savedEmojiSize) },
            { assertEquals(assertThrows.message, USER_NOT_FOUND.getMessage()) }
        )
        verify(userDao, times(2)).getUser(username)
        verify(userDao, times(1)).getUser(wrongUsername)
        for (emojiId in user.created_emojis) {
            verify(emojiDao, times(1)).getEmoji(emojiId)
        }
        for (emojiId in user.saved_emojis) {
            verify(emojiDao, times(1)).getEmoji(emojiId)
        }
    }

    @Test
    @DisplayName("특정 이모지 데이터 가져오기")
    fun getEmoji() {
        // given
        val emoji = testDto.emojiList[0]
        val id = emoji.id
        val wrongId = "wrong_id"
        Mockito.`when`(emojiDao.existsEmoji(id)).thenReturn(true)
        Mockito.`when`(emojiDao.existsEmoji(wrongId)).thenReturn(false)
        Mockito.`when`(emojiDao.getEmoji(id)).thenReturn(emoji)

        // when
        val result = emojiService.getEmoji(id)
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            emojiService.getEmoji(wrongId)
        }

        // then
        assertAll(
            { assertEquals(result, emoji) },
            { assertEquals(assertThrows.message, EMOJI_NOT_FOUND.getMessage()) }
        )
        verify(emojiDao, times(1)).existsEmoji(id)
        verify(emojiDao, times(1)).existsEmoji(wrongId)
        verify(emojiDao, times(1)).getEmoji(id)
    }

    @Test
    @DisplayName("이모지 올리기")
    fun postEmoji() {
        // given
        val username = "test_username"
        val audioContent = ByteArray(100)
        val file = MockMultipartFile("file", "test.mp4", "audio/mp4", audioContent)
        val imageContent = ByteArray(100)
        val thumbnail = MockMultipartFile("thumbnail", "test.jpeg", "image/jpeg", imageContent)
        val emojiUnicode = "test_emoji_unicode"
        val emojiLabel = "test_emoji_label"
        val emoji = testDto.emojiList[0]
        Mockito.`when`(emojiDao.insertEmoji(any(), any(), any(), any(), any(), any())).thenReturn(emoji)

        // when
        emojiService.postEmoji(username, file, thumbnail, emojiUnicode, emojiLabel)

        // then
        verify(emojiDao, times(1)).insertEmoji(any(), any(), any(), any(), any(), any())
        verify(userDao, times(1)).insertId(username, emoji.id, CREATED_EMOJIS.string)
    }

    @Test
    @DisplayName("이모지 저장하기 성공")
    fun saveEmojiSucceed() {
        // given
        val user = testDto.userList[0]
        val username = user.username
        val emojiId = "test_emojiId"
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        emojiService.saveEmoji(username, emojiId)

        // then
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
        verify(emojiDao, times(1)).numSavedChange(emojiId, 1)
    }

    @Test
    @DisplayName("이모지 저장하기 실패1: 이모지 없음")
    fun saveEmojiFail1() {
        // given
        val username = "test_username"
        val wrongId = "wrong_emojiId"
        Mockito.`when`(emojiDao.existsEmoji(wrongId)).thenReturn(false)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            emojiService.saveEmoji(username, wrongId)
        }

        // then
        assertEquals(assertThrows.message, EMOJI_NOT_FOUND.getMessage())
        verify(emojiDao, times(1)).existsEmoji(wrongId)
    }

    @Test
    @DisplayName("이모지 저장하기 실패2: 유저 없음")
    fun saveEmojiFail2() {
        // given
        val wrongUsername = "wrong_username"
        val emojiId = "test_emojiId"
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(wrongUsername)).thenReturn(null)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            emojiService.saveEmoji(wrongUsername, emojiId)
        }

        // then
        assertEquals(assertThrows.message, USER_NOT_FOUND.getMessage())
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(wrongUsername)
    }

    @Test
    @DisplayName("이모지 저장하기 실패3: 자신이 만든 이모지")
    fun saveEmojiFail3() {
        // given
        val user = testDto.userList[0]
        val username = user.username
        val emojiId = user.created_emojis[0]
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.saveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, USER_CREATED.getMessage())
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 저장하기 실패4: 이미 저장한 이모지")
    fun saveEmojiFail4() {
        // given
        val user = testDto.userList[0]
        val username = user.username
        val emojiId = user.saved_emojis[0]
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.saveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, USER_ALREADY_SAVED.getMessage())
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 저장 취소하기 성공")
    fun unSaveEmojiSucceed() {
        // given
        val user = testDto.userList[0]
        val username = user.username
        val emojiId = user.saved_emojis[0]
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        emojiService.unSaveEmoji(username, emojiId)

        // then
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
        verify(emojiDao, times(1)).numSavedChange(emojiId, -1)
    }

    @Test
    @DisplayName("이모지 저장 취소하기 실패1: 이모지 없음")
    fun unSaveEmojiFail1() {
        // given
        val username = "test_username"
        val wrongId = "wrong_emojiId"
        Mockito.`when`(emojiDao.existsEmoji(wrongId)).thenReturn(false)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            emojiService.unSaveEmoji(username, wrongId)
        }

        // then
        assertEquals(assertThrows.message, EMOJI_NOT_FOUND.getMessage())
        verify(emojiDao, times(1)).existsEmoji(wrongId)
    }

    @Test
    @DisplayName("이모지 저장하기 실패2: 유저 없음")
    fun unSaveEmojiFail2() {
        // given
        val wrongUsername = "wrong_username"
        val emojiId = "test_emojiId"
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(wrongUsername)).thenReturn(null)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            emojiService.unSaveEmoji(wrongUsername, emojiId)
        }

        // then
        assertEquals(assertThrows.message, USER_NOT_FOUND.getMessage())
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(wrongUsername)
    }

    @Test
    @DisplayName("이모지 저장 취소하기 실패3: 자신이 만든 이모지")
    fun unSaveEmojiFail3() {
        // given
        val user = testDto.userList[0]
        val username = user.username
        val emojiId = user.created_emojis[0]
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.unSaveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, USER_CREATED.getMessage())
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 저장 취소하기 실패4: 저장하지 않은 이모지")
    fun unSaveEmojiFail4() {
        // given
        val user = testDto.userList[0]
        val username = user.username
        val emojiId = "test_emojiId"
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.unSaveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, USER_ALREADY_UNSAVED.getMessage())
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 삭제하기")
    fun deleteEmoji() {
        // given
        val emoji = testDto.emojiList[0]
        val username = emoji.created_by
        val wrongUsername = "wrong_username"
        val emojiId = emoji.id
        val wrongId = "wrong_emojiId"
        val fileBlobName = username + "_" + emoji.created_at + ".mp4"
        val thumbnailBlobName = username + "_" + emoji.created_at + ".jpeg"
        Mockito.`when`(emojiDao.getEmoji(emojiId)).thenReturn(emoji)
        Mockito.`when`(emojiDao.getEmoji(wrongId)).thenReturn(null)
        val reactions = mutableListOf<ReactionDto>()
        for (reaction in testDto.reactionList) {
            if (reaction.emoji_id == emojiId) {
                reactions.add(reaction)
            }
        }
        Mockito.`when`(reactionDao.getReactionsWithField(emojiId, EMOJI_ID.string)).thenReturn(reactions)

        // when
        emojiService.deleteEmoji(username, emojiId)
        val assertThrows1 = assertThrows(CustomHttp404::class.java) {
            emojiService.deleteEmoji(username, wrongId)
        }
        val assertThrows2 = assertThrows(CustomHttp403::class.java) {
            emojiService.deleteEmoji(wrongUsername, emojiId)
        }

        // then
        assertAll(
            { assertEquals(assertThrows1.message, EMOJI_NOT_FOUND.getMessage()) },
            { assertEquals(assertThrows2.message, EMOJI_DELETE_FORBIDDEN.getMessage()) }
        )
        verify(emojiDao, times(2)).getEmoji(emojiId)
        verify(emojiDao, times(1)).getEmoji(wrongId)
        verify(emojiDao, times(1)).deleteFileInStorage(fileBlobName)
        verify(emojiDao, times(1)).deleteFileInStorage(thumbnailBlobName)
        verify(reactionDao, times(1)).getReactionsWithField(emojiId, EMOJI_ID.string)
        for (reaction in reactions) {
            verify(postDao, times(1)).deleteReaction(reaction.post_id, reaction.id)
            verify(reactionDao, times(1)).deleteReaction(reaction.id)
        }
        verify(userDao, times(1)).deleteAllSavedEmojiId(emojiId)
        verify(userDao, times(1)).deleteId(username, emojiId, CREATED_EMOJIS.string)
        verify(emojiDao, times(1)).deleteEmoji(emojiId)
    }
}