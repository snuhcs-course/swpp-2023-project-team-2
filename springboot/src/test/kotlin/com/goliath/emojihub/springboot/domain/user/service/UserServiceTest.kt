package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.auth.JwtTokenProvider
import com.goliath.emojihub.springboot.global.exception.CustomHttp401
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.CustomHttp409
import com.goliath.emojihub.springboot.global.exception.ErrorType.Conflict.ID_EXIST
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.ID_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.Unauthorized.PASSWORD_INCORRECT
import com.goliath.emojihub.springboot.global.util.StringValue.ReactionField.CREATED_BY
import com.goliath.emojihub.springboot.global.util.StringValue.ReactionField.EMOJI_ID
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

    @MockBean
    lateinit var reactionDao: ReactionDao

    companion object {
        val testDto = TestDto()
    }

    @Test
    @DisplayName("유저 데이터 가져오기")
    fun getUsers() {
        // given
        Mockito.`when`(userDao.getUsers()).thenReturn(testDto.userList)

        // when
        val result = userService.getUsers()

        // then
        assertEquals(result, testDto.userList)
        verify(userDao, times(1)).getUsers()
    }

    @Test
    @DisplayName("자신의 유저 데이터 가져오기")
    fun getMe() {
        // given
        val user = testDto.userList[0]
        val wrongUsername = "wrong_username"
        Mockito.`when`(userDao.getUser(user.username)).thenReturn(user)
        Mockito.`when`(userDao.getUser(wrongUsername)).thenReturn(null)

        // when
        val result = userService.getMe(user.username)
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            userService.getMe(wrongUsername)
        }

        // then
        assertAll(
            { assertEquals(result, user) },
            { assertEquals(assertThrows.message, USER_NOT_FOUND.getMessage()) }
        )
        verify(userDao, times(1)).getUser(user.username)
        verify(userDao, times(1)).getUser(wrongUsername)
    }

    @Test
    @DisplayName("회원가입 실패: 아이디 중복")
    fun signUpFail() {
        // given
        val user = testDto.userList[0]
        Mockito.`when`(userDao.existUser(user.username)).thenReturn(true)

        // when
        val assertThrows = assertThrows(CustomHttp409::class.java) {
            userService.signUp(user.email, user.username, user.password)
        }

        // then
        assertEquals(assertThrows.message, ID_EXIST.getMessage())
        verify(userDao, times(1)).existUser(user.username)
    }

    @Test
    @DisplayName("회원가입 성공")
    fun signUpSucceed() {
        // given
        val user = testDto.userList[0]
        val authToken = "test_authToken"
        val encodedPassword = "test_encoded_password"
        Mockito.`when`(userDao.existUser(user.username)).thenReturn(false)
        Mockito.`when`(passwordEncoder.encode(user.password)).thenReturn(encodedPassword)
        Mockito.`when`(jwtTokenProvider.createToken(user.username)).thenReturn(authToken)

        // when
        val result = userService.signUp(user.email, user.username, user.password)

        // then
        assertEquals(result.accessToken, authToken)
        verify(userDao, times(1)).existUser(user.username)
        verify(passwordEncoder, times(1)).encode(user.password)
        verify(jwtTokenProvider, times(1)).createToken(user.username)
    }

    @Test
    @DisplayName("로그인 실패1: 아이디 존재 X")
    fun loginFail1() {
        // given
        val user = testDto.userList[0]
        Mockito.`when`(userDao.getUser(user.username)).thenReturn(null)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            userService.login(user.username, user.password)
        }

        // then
        assertEquals(assertThrows.message, ID_NOT_FOUND.getMessage())
        verify(userDao, times(1)).getUser(user.username)
    }

    @Test
    @DisplayName("로그인 실패2: 비밀번호 불일치")
    fun loginFail2() {
        // given
        val user = testDto.userList[0]
        val wrongPassword = "wrong_password"
        Mockito.`when`(userDao.getUser(user.username)).thenReturn(user)
        Mockito.`when`(passwordEncoder.matches(wrongPassword, user.password)).thenReturn(false)

        // when
        val assertThrows = assertThrows(CustomHttp401::class.java) {
            userService.login(user.username, wrongPassword)
        }

        // then
        assertEquals(assertThrows.message, PASSWORD_INCORRECT.getMessage())
        verify(userDao, times(1)).getUser(user.username)
        verify(passwordEncoder, times(1)).matches(wrongPassword, user.password)
    }

    @Test
    @DisplayName("로그인 성공")
    fun loginSucceed() {
        // given
        val user = testDto.userList[0]
        val authToken = "test_authToken"
        Mockito.`when`(userDao.getUser(user.username)).thenReturn(user)
        Mockito.`when`(passwordEncoder.matches(user.password, user.password)).thenReturn(true)
        Mockito.`when`(jwtTokenProvider.createToken(user.username)).thenReturn(authToken)

        // when
        val result = userService.login(user.username, user.password)

        // then
        assertEquals(result.accessToken, authToken)
        verify(userDao, times(1)).getUser(user.username)
        verify(passwordEncoder, times(1)).matches(user.password, user.password)
        verify(jwtTokenProvider, times(1)).createToken(user.username)
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
        val username = "wrong_username"
        Mockito.`when`(userDao.getUser(username)).thenReturn(null)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            userService.signOut(username)
        }

        // then
        assertEquals(assertThrows.message, USER_NOT_FOUND.getMessage())
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    fun signOut() {
        // given
        val n = 0
        val user = testDto.userList[n]
        val username = user.username

        val myReactions = mutableListOf<ReactionDto>()
        for (reactions in testDto.reactionList) {
            if (reactions.created_by == username) {
                myReactions.add(reactions)
            }
        }

        Mockito.`when`(userDao.getUser(user.username)).thenReturn(user)
        Mockito.`when`(reactionDao.getReactionsWithField(user.username, CREATED_BY.string)).thenReturn(myReactions)
        for (post in testDto.postList) {
            Mockito.`when`(postDao.getPost(post.id)).thenReturn(post)
        }
        for (reaction in testDto.reactionList) {
            Mockito.`when`(reactionDao.getReaction(reaction.id)).thenReturn(reaction)
        }
        for (emoji in testDto.emojiList) {
            Mockito.`when`(emojiDao.getEmoji(emoji.id)).thenReturn(emoji)
            Mockito.`when`(emojiDao.existsEmoji(emoji.id)).thenReturn(true)
        }
        for (userDto in testDto.userList) {
            val firstEmojiId = userDto.created_emojis!![0]
            val reactions = mutableListOf<ReactionDto>()
            for (reaction in testDto.reactionList) {
                if (reaction.emoji_id == firstEmojiId) {
                    reactions.add(reaction)
                }
            }
            Mockito.`when`(reactionDao.getReactionsWithField(firstEmojiId, EMOJI_ID.string)).thenReturn(reactions)
        }

        // when
        val result = userService.signOut(username)

        // then
        assertEquals(result, Unit)
        verify(userDao, times(1)).getUser(username)
        verify(userDao, times(1)).deleteUser(username)
    }
}