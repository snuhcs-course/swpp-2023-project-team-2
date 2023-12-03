package com.goliath.emojihub.repositories.remote

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.PostFetchType
import com.goliath.emojihub.data_sources.PostPagingSource
import com.goliath.emojihub.data_sources.api.PostApi
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface PostRepository {
    suspend fun fetchPostList(): Flow<PagingData<PostDto>>
    suspend fun fetchMyPostList(): Flow<PagingData<PostDto>>
    suspend fun uploadPost(dto: UploadPostDto): Response<Unit>
    suspend fun getPostWithId(id: String): PostDto?
    suspend fun editPost(id: String, content: String)
    suspend fun deletePost(id: String)
}

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
): PostRepository {
    override suspend fun fetchPostList(): Flow<PagingData<PostDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { PostPagingSource(postApi, PostFetchType.GENERAL) }
        ).flow
    }

    override suspend fun fetchMyPostList(): Flow<PagingData<PostDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { PostPagingSource(postApi, PostFetchType.MY) }
        ).flow
    }

    override suspend fun uploadPost(dto: UploadPostDto): Response<Unit> {
        return postApi.uploadPost(dto)
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