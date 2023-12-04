package com.goliath.emojihub.springboot.domain.reaction.service

import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.USER_ALREADY_REACT
import com.goliath.emojihub.springboot.global.exception.ErrorType.Forbidden.REACTION_DELETE_FORBIDDEN
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.POST_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.EMOJI_NOT_FOUND
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.REACTION_NOT_FOUND
import org.springframework.stereotype.Service

@Service
class ReactionService(
    private val reactionDao: ReactionDao,
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val emojiDao: EmojiDao
) {
    companion object {
        const val POST_ID = "post_id"
    }


    fun getReactionsOfPost(postId: String): List<ReactionDto> {
        if (!postDao.existPost(postId)) throw CustomHttp404(POST_NOT_FOUND)
        return reactionDao.getReactionsWithField(postId, POST_ID)
    }

    fun postReaction(username: String, postId: String, emojiId: String) {
        if (!userDao.existUser(username)) throw CustomHttp404(USER_NOT_FOUND)
        if (!postDao.existPost(postId)) throw CustomHttp404(POST_NOT_FOUND)
        val emoji = emojiDao.getEmoji(emojiId) ?: throw CustomHttp404(EMOJI_NOT_FOUND)
        if (reactionDao.existSameReaction(username, postId, emojiId)) throw CustomHttp403(USER_ALREADY_REACT)
        val reaction = reactionDao.insertReaction(username, postId, emojiId)
        postDao.insertReaction(postId, reaction.id, emoji.emoji_unicode)
    }

    fun deleteReaction(username: String, reactionId: String) {
        val reaction = reactionDao.getReaction(reactionId) ?: throw CustomHttp404(REACTION_NOT_FOUND)
        if (username != reaction.created_by)
            throw CustomHttp403(REACTION_DELETE_FORBIDDEN)
        // delete reaction id in post
        postDao.deleteReaction(reaction.post_id, reactionId)
        // delete reaction
        reactionDao.deleteReaction(reactionId)
    }
}