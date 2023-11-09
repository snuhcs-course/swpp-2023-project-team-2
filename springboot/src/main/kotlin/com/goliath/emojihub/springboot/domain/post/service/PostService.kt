package com.goliath.emojihub.springboot.domain.post.service

import com.goliath.emojihub.springboot.domain.post.dao.PostDao
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.post.dto.PostRequest
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp403
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import org.springframework.stereotype.Service

@Service
class PostService(private val postDao: PostDao) {
    fun postPost(username: String, postRequest: PostRequest) {
        postDao.postPost(username, postRequest)
    }

    fun getPosts(index: Int, count: Int): List<PostDto> {
        if (index <= 0) throw CustomHttp400("Index should be positive integer.")
        // count는 0보다 커야 함
        if (count < 0) throw CustomHttp400("Count should be bigger than 0.")
        return postDao.getPosts(index, count)
    }

    fun getPost(id: String): PostDto? {
        if (postDao.existPost(id).not())
            throw CustomHttp404("Post doesn't exist.")
        return postDao.getPost(id)
    }

    fun patchPost(username: String, id: String, postRequest: PostRequest) {
        val post = postDao.getPost(id) ?: throw CustomHttp404("Post doesn't exist.")
        if (username != post.created_by)
            throw CustomHttp403("You can't update this post.")
        postDao.updatePost(id, postRequest)
    }

    fun deletePost(username: String, id: String) {
        val post = postDao.getPost(id) ?: throw CustomHttp404("Post doesn't exist.")
        if (username != post.created_by)
            throw CustomHttp403("You can't delete this post.")
        postDao.deletePost(id)
    }
}