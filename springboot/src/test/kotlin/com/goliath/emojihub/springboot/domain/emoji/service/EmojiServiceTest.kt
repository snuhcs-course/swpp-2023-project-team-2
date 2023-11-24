package com.goliath.emojihub.springboot.domain.emoji.service

import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
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

    companion object {
        const val CREATED_EMOJIS = "created_emojis"
        const val SAVED_EMOJIS = "saved_emojis"
        var emojiList: MutableList<EmojiDto> = mutableListOf()
        const val size = 2

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            for (i in 0 until size) {
                emojiList.add(
                    EmojiDto(
                        id = "test_id$i",
                        created_by = "test_created_by$i",
                        video_url = "test_video_url$i",
                        emoji_unicode = "test_video_url$i",
                        emoji_label = "test_emoji_label$i",
                        created_at = "test_created_at$i",
                        num_saved = i
                    )
                )
            }
        }
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
        Mockito.`when`(emojiDao.getEmojis(sortByDate, index, count)).thenReturn(emojiList)

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
            { assertEquals(result, emojiList) },
            { assertEquals(assertThrows1.message, "Index should be positive integer.") },
            { assertEquals(assertThrows2.message, "Count should be positive integer.") }
        )
        verify(emojiDao, times(1)).getEmojis(sortByDate, index, count)
    }

    @Test
    @DisplayName("자신의 이모지 데이터 가져오기")
    fun getMyEmojis() {
        // given
        val emoji = emojiList[0]
        val username = emoji.created_by
        val wrongUsername = "wrong_username"
        val user = UserDto(
            username = username,
            email = "test_email",
            password = "test_password"
        )
        user.created_emojis!!.add(emojiList[0].id)
        user.saved_emojis!!.add(emojiList[1].id)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)
        Mockito.`when`(userDao.getUser(wrongUsername)).thenReturn(null)
        Mockito.`when`(emojiDao.getEmoji(user.created_emojis!![0])).thenReturn(emojiList[0])
        Mockito.`when`(emojiDao.getEmoji(user.saved_emojis!![0])).thenReturn(emojiList[1])

        // when
        val createdEmojisResult = emojiService.getMyEmojis(username, CREATED_EMOJIS)
        val savedEmojisResult = emojiService.getMyEmojis(username, SAVED_EMOJIS)
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            emojiService.getMyEmojis(wrongUsername, CREATED_EMOJIS)
        }

        // then
        assertAll(
            { assertEquals(createdEmojisResult.size, 1) },
            { assertEquals(savedEmojisResult.size, 1) },
            { assertEquals(createdEmojisResult[0], emojiList[0]) },
            { assertEquals(savedEmojisResult[0], emojiList[1]) },
            { assertEquals(assertThrows.message, "User doesn't exist.") }
        )
        verify(userDao, times(2)).getUser(username)
        verify(userDao, times(1)).getUser(wrongUsername)
        verify(emojiDao, times(1)).getEmoji(user.created_emojis!![0])
        verify(emojiDao, times(1)).getEmoji(user.saved_emojis!![0])
    }

    @Test
    @DisplayName("특정 이모지 데이터 가져오기")
    fun getEmoji() {
        // given
        val emoji = emojiList[0]
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
            { assertEquals(assertThrows.message, "Emoji doesn't exist.") }
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
        val emoji = emojiList[0]
        Mockito.`when`(emojiDao.insertEmoji(any(), any(), any(), any(), any(), any())).thenReturn(emoji)

        // when
        emojiService.postEmoji(username, file, thumbnail, emojiUnicode, emojiLabel)

        // then
        verify(emojiDao, times(1)).insertEmoji(any(), any(), any(), any(), any(), any())
        verify(userDao, times(1)).insertId(username, emoji.id, CREATED_EMOJIS)
    }

    @Test
    @DisplayName("이모지 저장하기 성공")
    fun saveEmojiSucceed() {
        // given
        val username = "test_username"
        val emojiId = "test_emojiId"
        val user = UserDto(
            email = "test_email",
            username = username,
            password = "different_encoded_password"
        )
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
        assertEquals(assertThrows.message, "Emoji doesn't exist.")
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
        assertEquals(assertThrows.message, "User doesn't exist.")
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(wrongUsername)
    }

    @Test
    @DisplayName("이모지 저장하기 실패3: 자신이 만든 이모지")
    fun saveEmojiFail3() {
        // given
        val username = "test_username"
        val emojiId = "test_emojiId"
        val user = UserDto(
            email = "test_email",
            username = username,
            password = "different_encoded_password"
        )
        user.created_emojis!!.add(emojiId)
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.saveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, "User created this emoji.")
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 저장하기 실패4: 이미 저장한 이모지")
    fun saveEmojiFail4() {
        // given
        val username = "test_username"
        val emojiId = "test_emojiId"
        val user = UserDto(
            email = "test_email",
            username = username,
            password = "different_encoded_password"
        )
        user.saved_emojis!!.add(emojiId)
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.saveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, "User already saved this emoji.")
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 저장 취소하기 성공")
    fun unSaveEmojiSucceed() {
        // given
        val username = "test_username"
        val emojiId = "test_emojiId"
        val user = UserDto(
            email = "test_email",
            username = username,
            password = "different_encoded_password"
        )
        user.saved_emojis!!.add(emojiId)
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
        assertEquals(assertThrows.message, "Emoji doesn't exist.")
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
        assertEquals(assertThrows.message, "User doesn't exist.")
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(wrongUsername)
    }

    @Test
    @DisplayName("이모지 저장 취소하기 실패3: 자신이 만든 이모지")
    fun unSaveEmojiFail3() {
        // given
        val username = "test_username"
        val emojiId = "test_emojiId"
        val user = UserDto(
            email = "test_email",
            username = username,
            password = "different_encoded_password"
        )
        user.created_emojis!!.add(emojiId)
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.unSaveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, "User created this emoji.")
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 저장 취소하기 실패4: 저장하지 않은 이모지")
    fun unSaveEmojiFail4() {
        // given
        val username = "test_username"
        val emojiId = "test_emojiId"
        val user = UserDto(
            email = "test_email",
            username = username,
            password = "different_encoded_password"
        )
        Mockito.`when`(emojiDao.existsEmoji(emojiId)).thenReturn(true)
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val assertThrows = assertThrows(CustomHttp403::class.java) {
            emojiService.unSaveEmoji(username, emojiId)
        }

        // then
        assertEquals(assertThrows.message, "User already unsaved this emoji.")
        verify(emojiDao, times(1)).existsEmoji(emojiId)
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("이모지 삭제하기")
    fun deleteEmoji() {
        // given
        val emoji = emojiList[0]
        val username = emoji.created_by
        val wrongUsername = "wrong_username"
        val emojiId = emoji.id
        val wrongId = "wrong_emojiId"
        Mockito.`when`(emojiDao.getEmoji(emojiId)).thenReturn(emoji)
        Mockito.`when`(emojiDao.getEmoji(wrongId)).thenReturn(null)

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
            { assertEquals(assertThrows1.message, "Emoji doesn't exist.") },
            { assertEquals(assertThrows2.message, "You can't delete this emoji.") }
        )
        verify(emojiDao, times(2)).getEmoji(emojiId)
        verify(emojiDao, times(1)).getEmoji(wrongId)
        verify(emojiDao, times(1)).deleteFileInStorage(any())
        verify(emojiDao, times(1)).deleteEmoji(emojiId)
    }
}