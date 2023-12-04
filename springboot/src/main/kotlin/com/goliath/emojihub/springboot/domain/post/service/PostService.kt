package com.goliath.emojihub.springboot.domain.post.service

import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.reaction.dao.ReactionDao
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val reactionDao: ReactionDao
) {

    companion object {
        const val CREATED_POSTS = "created_posts"
    }

    fun postPost(username: String, content: String) {
        val post = postDao.insertPost(username, content)
        userDao.insertId(username, post.id, CREATED_POSTS)
    }

    fun getPosts(index: Int, count: Int): List<PostDto> {
        if (index <= 0) throw CustomHttp400("Index should be positive integer.")
        if (count <= 0) throw CustomHttp400("Count should be positive integer.")
        return postDao.getPosts(index, count)
    }

    fun getMyPosts(username: String, index: Int, count: Int): List<PostDto> {
        if (index <= 0) throw CustomHttp400("Index should be positive integer.")
        if (count <= 0) throw CustomHttp400("Count should be positive integer.")
        if (!userDao.existUser(username)) throw CustomHttp404("User doesn't exist.")
        return postDao.getMyPosts(username, index, count)
    }

    fun getPost(postId: String): PostDto? {
        if (postDao.existPost(postId).not())
            throw CustomHttp404("Post doesn't exist.")
        return postDao.getPost(postId)
    }

    fun patchPost(username: String, postId: String, content: String) {
        val post = postDao.getPost(postId) ?: throw CustomHttp404("Post doesn't exist.")
        if (username != post.created_by)
            throw CustomHttp403("You can't update this post.")
        postDao.updatePost(postId, content)
    }

    fun deletePost(username: String, postId: String) {
        val post = postDao.getPost(postId) ?: throw CustomHttp404("Post doesn't exist.")
        if (username != post.created_by)
            throw CustomHttp403("You can't delete this post.")
        // delete post(and post's reactions)
        val reactionWithEmojiUnicodes = post.reactions
        for (reactionWithEmojiUnicode in reactionWithEmojiUnicodes) {
            val reaction = reactionDao.getReaction(reactionWithEmojiUnicode.id) ?: continue
            if (postId != reaction.post_id) continue
            reactionDao.deleteReaction(reactionWithEmojiUnicode.id)
        }
        // delete created_post id in user
        userDao.deleteId(username, postId, CREATED_POSTS)
        // delete post
        postDao.deletePost(postId)
    }
}