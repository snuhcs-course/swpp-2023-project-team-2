package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp401
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.CustomHttp409
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.auth.JwtTokenProvider
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userDao: UserDao,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val emojiDao: EmojiDao,
    private val postDao: PostDao,
) {
    fun getUsers(): List<UserDto> {
        return userDao.getUsers()
    }

    fun signUp(email: String, username: String, password: String): UserDto.AuthToken {
        if (userDao.existUser(username)) {
            throw CustomHttp409("Id already exists.")
        }
        val encodedPassword = passwordEncoder.encode(password)
        val user = UserDto(
            email = email,
            username = username,
            password = encodedPassword
        )
        userDao.insertUser(user)
        val authToken = jwtTokenProvider.createToken(username)
        return UserDto.AuthToken(authToken)
    }

    fun login(username: String, password: String): UserDto.AuthToken {
        val user = userDao.getUser(username) ?: throw CustomHttp404("Id doesn't exist.")
        if (!passwordEncoder.matches(password, user.password)) {
            throw CustomHttp401("Password is incorrect.")
        }
        val authToken = jwtTokenProvider.createToken(user.username)
        return UserDto.AuthToken(authToken)
    }

    fun logout() {
        return
    }

    fun signOut(username: String) {
        val user = userDao.getUser(username) ?: throw CustomHttp404("User doesn't exist.")
        val emojiIds = user.created_emojis
        val postIds = user.created_posts
        if (emojiIds != null) {
            for (emojiId in emojiIds) {
                val emoji = emojiDao.getEmoji(emojiId) ?: continue
                if (username != emoji.created_by) throw CustomHttp403("You can't delete this emoji.")
                val blobName = username + "_" + emoji.created_at + ".mp4"
                emojiDao.deleteFileInStorage(blobName)
                emojiDao.deleteEmoji(username, emojiId)
            }
        }
        if (postIds != null) {
            for (postId in postIds) {
                val post = postDao.getPost(postId) ?: continue
                if (username != post.created_by) throw CustomHttp403("You can't delete this post.")
                postDao.deletePost(postId)
            }
        }
        return userDao.deleteUser(username)
    }
}