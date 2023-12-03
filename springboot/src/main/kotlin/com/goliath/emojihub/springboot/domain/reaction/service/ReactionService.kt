package com.goliath.emojihub.springboot.domain.reaction.service

import com.goliath.emojihub.springboot.domain.emoji.dao.EmojiDao
import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
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
        if (!postDao.existPost(postId)) throw CustomHttp404("Post doesn't exist.")
        return reactionDao.getReactionsWithField(postId, POST_ID)
    }

    fun postReaction(username: String, postId: String, emojiId: String) {
        if (!userDao.existUser(username)) throw CustomHttp404("User doesn't exist.")
        if (!postDao.existPost(postId)) throw CustomHttp404("Post doesn't exist.")
        if (!emojiDao.existsEmoji(emojiId)) throw CustomHttp404("Emoji doesn't exist.")
        if (reactionDao.existSameReaction(username, postId, emojiId)) throw CustomHttp403("User already react to this post with this emoji.")
        val reaction = reactionDao.insertReaction(username, postId, emojiId)
        postDao.insertReactionId(postId, reaction.id)
    }

    fun deleteReaction(username: String, reactionId: String) {
        val reaction = reactionDao.getReaction(reactionId) ?: throw CustomHttp404("Reaction doesn't exist.")
        if (username != reaction.created_by)
            throw CustomHttp403("You can't delete this reaction.")
        // delete reaction id in post
        postDao.deleteReactionId(reaction.post_id, reactionId)
        // delete reaction
        reactionDao.deleteReaction(reactionId)
    }
}