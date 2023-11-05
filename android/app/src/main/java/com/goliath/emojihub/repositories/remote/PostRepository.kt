package com.goliath.emojihub.repositories.remote

import android.util.Log
import com.goliath.emojihub.data_sources.api.PostApi
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import javax.inject.Inject
import javax.inject.Singleton

interface PostRepository {
    suspend fun fetchPostList(numLimit: Int): List<PostDto>
    suspend fun uploadPost(content: String): Boolean
    suspend fun getPostWithId(id: String): PostDto?
    suspend fun editPost(id: String, content: String)
    suspend fun deletePost(id: String)
}

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
): PostRepository {
    override suspend fun fetchPostList(numLimit: Int): List<PostDto> {
        return postApi.fetchPostList(numLimit).body() ?: listOf()
    }

    override suspend fun uploadPost(content: String): Boolean {
        val dto = UploadPostDto(content)
        return postApi.uploadPost(dto).isSuccessful
    }

    override suspend fun getPostWithId(id: String): PostDto? {
        val result = postApi.getPostWithId(id)
        if (result.isSuccessful) {
            Log.d("Search Post Success", result.body().toString())
            return result.body()
        } else {
            Log.d("Search Post Failure", result.raw().toString())
        }
        return null
    }

    override suspend fun editPost(id: String, content: String) {
        val dto = UploadPostDto(content)
        postApi.editPost(id, dto)
    }

    override suspend fun deletePost(id: String) {
        postApi.deletePost(id)
    }
}