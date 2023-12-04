package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp401
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.CustomHttp409
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.auth.JwtTokenProvider
import com.goliath.emojihub.springboot.global.exception.ErrorType.Unauthorized.PASSWORD_INCORRECT
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.ID_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.Conflict.ID_EXIST
import com.goliath.emojihub.springboot.global.util.StringValue.ReactionField.CREATED_BY
import com.goliath.emojihub.springboot.global.util.StringValue.ReactionField.EMOJI_ID
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userDao: UserDao,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val emojiDao: EmojiDao,
    private val postDao: PostDao,
    private val reactionDao: ReactionDao,
) {

    fun getUsers(): List<UserDto> {
        return userDao.getUsers()
    }

    fun signUp(email: String, username: String, password: String): UserDto.AuthToken {
        if (userDao.existUser(username)) {
            throw CustomHttp409(ID_EXIST)
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
        val user = userDao.getUser(username) ?: throw CustomHttp404(ID_NOT_FOUND)
        if (!passwordEncoder.matches(password, user.password)) {
            throw CustomHttp401(PASSWORD_INCORRECT)
        }
        val authToken = jwtTokenProvider.createToken(user.username)
        return UserDto.AuthToken(authToken)
    }

    fun logout() {
        return
    }

    fun signOut(username: String) {
        val user = userDao.getUser(username) ?: throw CustomHttp404(USER_NOT_FOUND)
        val createdEmojiIds = user.created_emojis
        val savedEmojiIds = user.saved_emojis
        val postIds = user.created_posts
        // delete all reactions(and reaction id in posts) created by user
        val myReactions = reactionDao.getReactionsWithField(username, CREATED_BY.string)
        for (reaction in myReactions) {
            postDao.deleteReaction(reaction.post_id, reaction.id)
            reactionDao.deleteReaction(reaction.id)
        }
        // delete all posts(and posts' reactions) created by user
        if (postIds != null) {
            for (postId in postIds) {
                val post = postDao.getPost(postId) ?: continue
                if (username != post.created_by) continue
                val reactionWithEmojiUnicodes = post.reactions
                for (reactionWithEmojiUnicode in reactionWithEmojiUnicodes) {
                    val reaction = reactionDao.getReaction(reactionWithEmojiUnicode.id) ?: continue
                    if (postId != reaction.post_id) continue
                    reactionDao.deleteReaction(reactionWithEmojiUnicode.id)
                }
                postDao.deletePost(postId)
            }
        }
        // delete all emojis(and reactions(and reaction id in posts) using these emojis) created by user
        if (createdEmojiIds != null) {
            for (emojiId in createdEmojiIds) {
                val emoji = emojiDao.getEmoji(emojiId) ?: continue
                if (username != emoji.created_by) continue
                val fileBlobName = username + "_" + emoji.created_at + ".mp4"
                val thumbnailBlobName = username + "_" + emoji.created_at + ".jpeg"
                emojiDao.deleteFileInStorage(fileBlobName)
                emojiDao.deleteFileInStorage(thumbnailBlobName)
                val reactions = reactionDao.getReactionsWithField(emojiId, EMOJI_ID.string)
                for (reaction in reactions) {
                    postDao.deleteReaction(reaction.post_id, reaction.id)
                    reactionDao.deleteReaction(reaction.id)
                }
                userDao.deleteAllSavedEmojiId(emojiId)
                emojiDao.deleteEmoji(emojiId)
            }
        }
        // unsave all emojis saved by user
        if (savedEmojiIds != null) {
            for (emojiId in savedEmojiIds) {
                if (!emojiDao.existsEmoji(emojiId)) continue
                emojiDao.numSavedChange(emojiId, -1)
            }
        }
        // delete user
        return userDao.deleteUser(username)
    }
}