package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.api.PostApi
import javax.inject.Inject
import javax.inject.Singleton

interface PostRepository {
    suspend fun uploadPost(content: String)
}

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
): PostRepository {
    override suspend fun uploadPost(content: String) {
        TODO("Not yet implemented")
    }
}