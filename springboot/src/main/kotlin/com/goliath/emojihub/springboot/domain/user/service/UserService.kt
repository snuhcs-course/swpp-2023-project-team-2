package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp401
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.CustomHttp409
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.dto.LoginRequest
import com.goliath.emojihub.springboot.domain.user.dto.SignUpRequest
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.auth.JwtTokenProvider
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface UserService {
    fun getUsers(): List<UserDto>
    fun signUp(signUpRequest: SignUpRequest): UserDto.AuthToken
    fun login(loginRequest: LoginRequest): UserDto.AuthToken
    fun logout()
    fun signOut(username: String)
}

@Service
class UserServiceImpl(
    private val userDao: UserDao,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val emojiDao: EmojiDao,
    ) : UserService {
    override fun getUsers(): List<UserDto> {
        return userDao.getUsers()
    }

    override fun signUp(signUpRequest: SignUpRequest): UserDto.AuthToken {
        if (userDao.existUser(signUpRequest.username)) {
            throw CustomHttp409("Id already exists.")
        }
        signUpRequest.password = passwordEncoder.encode(signUpRequest.password)
        userDao.insertUser(signUpRequest)
        val authToken = jwtTokenProvider.createToken(signUpRequest.username)
        return UserDto.AuthToken(authToken)
    }

    override fun login(loginRequest: LoginRequest): UserDto.AuthToken {
        val user = userDao.getUser(loginRequest.username) ?: throw CustomHttp404("Id doesn't exist.")
        if (!passwordEncoder.matches(loginRequest.password, user.password)) {
            throw CustomHttp401("Password is incorrect.")
        }
        val authToken = jwtTokenProvider.createToken(user.username)
        return UserDto.AuthToken(authToken)
    }

    override fun logout() {
        return
    }

    override fun signOut(username: String) {
        val user = userDao.getUser(username) ?: throw CustomHttp404("User doesn't exist.")
        val emojiIds = user.created_emojis ?: return userDao.deleteUser(username)
        for (emojiId in emojiIds) {
            val emoji = emojiDao.getEmoji(emojiId) ?: continue
            if (username != emoji.created_by) throw CustomHttp403("You can't delete this emoji.")
            val blobName = username + "_" + emoji.created_at + ".mp4"
            emojiDao.deleteFileInStorage(blobName)
            emojiDao.deleteEmoji(username, emojiId)
        }
        return userDao.deleteUser(username)
    }
}