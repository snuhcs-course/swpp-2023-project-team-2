package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.auth.JwtTokenProvider
import com.goliath.emojihub.springboot.global.exception.CustomHttp401
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.CustomHttp409
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
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(UserService::class)
internal class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    @MockBean
    lateinit var userDao: UserDao

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    lateinit var passwordEncoder: PasswordEncoder

    @MockBean
    lateinit var emojiDao: EmojiDao

    @MockBean
    lateinit var postDao: PostDao

    @Test
    @DisplayName("유저 데이터 가져오기")
    fun getUsers() {
        // given
        val list = mutableListOf<UserDto>()
        val size = 2
        val email = "test_email"
        val username = "test_username"
        val password = "test_password"
        for (i in 0 until size) {
            list.add(
                UserDto(
                    email = email + i,
                    username = username + i,
                    password = password + i,
                )
            )
        }
        Mockito.`when`(userDao.getUsers()).thenReturn(list)

        // when
        val result = userService.getUsers()

        // then
        assertAll(
            { assertEquals(result.size, size) },
            { assertEquals(result[0].email, email + 0) },
            { assertEquals(result[0].username, username + 0) },
            { assertEquals(result[0].password, password + 0) }
        )
        verify(userDao, times(1)).getUsers()
    }

    @Test
    @DisplayName("회원가입 실패: 아이디 중복")
    fun signUpFail() {
        // given
        val email = "test_email"
        val username = "test_username"
        val password = "test_password"
        Mockito.`when`(userDao.existUser(username)).thenReturn(true)

        // when
        val assertThrows = assertThrows(CustomHttp409::class.java) {
            userService.signUp(email, username, password)
        }

        // then
        assertEquals(assertThrows.message, "Id already exists.")
    }

    @Test
    @DisplayName("회원가입 성공")
    fun signUpSucceed() {
        // given
        val email = "test_email"
        val username = "test_username"
        val password = "test_password"
        val authToken = "test_authToken"
        Mockito.`when`(userDao.existUser(username)).thenReturn(false)
        val encodedPassword = "test_encoded_password"
        Mockito.`when`(passwordEncoder.encode(password)).thenReturn(encodedPassword)
        Mockito.`when`(jwtTokenProvider.createToken(username)).thenReturn(authToken)

        // when
        val result = userService.signUp(email, username, password)

        // then
        assertEquals(result.accessToken, authToken)
    }

    @Test
    @DisplayName("로그인 실패1: 아이디 존재 X")
    fun loginFail1() {
        // given
        val username = "test_username"
        val password = "test_password"
        Mockito.`when`(userDao.getUser(username)).thenReturn(null)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            userService.login(username, password)
        }

        // then
        assertEquals(assertThrows.message, "Id doesn't exist.")
    }

    @Test
    @DisplayName("로그인 실패2: 비밀번호 불일치")
    fun loginFail2() {
        // given
        val username = "test_username"
        val password = "test_password"
        val user = UserDto(
            email = "test_email",
            username = "test_username",
            password = "different_encoded_password"
        )
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)
        Mockito.`when`(passwordEncoder.matches(password, user.password)).thenReturn(false)

        // when
        val assertThrows = assertThrows(CustomHttp401::class.java) {
            userService.login(username, password)
        }

        // then
        assertEquals(assertThrows.message, "Password is incorrect.")
    }

    @Test
    @DisplayName("로그인 성공")
    fun loginSucceed() {
        // given
        val username = "test_username"
        val password = "test_password"
        val user = UserDto(
            email = "test_email",
            username = "test_username",
            password = "test_encoded_password"
        )
        val authToken = "test_authToken"
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)
        Mockito.`when`(passwordEncoder.matches(password, user.password)).thenReturn(true)
        Mockito.`when`(jwtTokenProvider.createToken(username)).thenReturn(authToken)


        // when
        val result = userService.login(username, password)


        // then
        assertEquals(result.accessToken, authToken)
    }

    @Test
    @DisplayName("로그아웃")
    fun logout() {
        // when
        val result = userService.logout()

        // then
        assertEquals(result, Unit)
    }

    @Test
    @DisplayName("회원탈퇴 실패: 유저 존재 X")
    fun signOutFail() {
        // given
        val username = "test_username"
        Mockito.`when`(userDao.getUser(username)).thenReturn(null)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            userService.signOut(username)
        }

        // then
        assertEquals(assertThrows.message, "User doesn't exist.")
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    fun signOut() {
        // given
        val username = "test_username"

        val createdEmojiIds = mutableListOf<String>()
        val savedEmojiIds = mutableListOf<String>()
        val postIds = mutableListOf<String>()

        val ceSize = 2
        val seSize = 2
        val pSize = 2
        val createdEmojis = mutableListOf<EmojiDto>()
        val createdPosts = mutableListOf<PostDto>()
        val blobNames = mutableListOf<String>()
        for (i in 0 until ceSize) {
            createdEmojiIds.add("test_createdEmojiId$i")
            createdEmojis.add(
                EmojiDto(
                    id = createdEmojiIds[i],
                    created_by = username,
                    created_at = "test_created_at$i"
                )
            )
            blobNames.add(
                username + "_" + createdEmojis[i].created_at + ".mp4"
            )
        }
        for (i in 0 until seSize)
            savedEmojiIds.add("test_savedEmojiId$i")
        for (i in 0 until pSize) {
            postIds.add("test_postId$i")
            createdPosts.add(
                PostDto(
                    id = postIds[i],
                    created_by = username,
                )
            )
        }
        val user = UserDto(
            email = "test_email",
            username = "test_username",
            password = "test_encoded_password",
            created_emojis = createdEmojiIds,
            saved_emojis = savedEmojiIds,
            created_posts = postIds
        )
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)
        for (i in 0 until ceSize) {
            Mockito.`when`(emojiDao.getEmoji(createdEmojiIds[i])).thenReturn(createdEmojis[i])
        }
        for (i in 0 until seSize) {
            Mockito.`when`(emojiDao.existsEmoji(savedEmojiIds[i])).thenReturn(true)
        }
        for (i in 0 until pSize) {
            Mockito.`when`(postDao.getPost(postIds[i])).thenReturn(createdPosts[i])
        }

        // when
        val result = userService.signOut(username)

        // then
        assertEquals(result, Unit)
        verify(userDao, times(1)).getUser(username)
        for (i in 0 until ceSize) {
            verify(emojiDao, times(1)).getEmoji(createdEmojiIds[i])
            verify(emojiDao, times(1)).deleteFileInStorage(blobNames[i])
            verify(emojiDao, times(1)).deleteEmoji(username, createdEmojiIds[i])
        }
        for (i in 0 until seSize) {
            verify(emojiDao, times(1)).existsEmoji(savedEmojiIds[i])
            verify(emojiDao, times(1)).unSaveEmoji(username, savedEmojiIds[i])
        }
        for (i in 0 until pSize) {
            verify(postDao, times(1)).getPost(postIds[i])
            verify(postDao, times(1)).deletePost(postIds[i])
        }
        verify(userDao, times(1)).deleteUser(username)
    }
}