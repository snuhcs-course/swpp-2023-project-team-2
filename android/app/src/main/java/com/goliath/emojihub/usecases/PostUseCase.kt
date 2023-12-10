package com.goliath.emojihub.usecases

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import com.goliath.emojihub.repositories.remote.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

interface PostUseCase {
    val postList: StateFlow<PagingData<Post>>
    val myPostList: StateFlow<PagingData<Post>>
    suspend fun updatePostList(data: PagingData<Post>)
    suspend fun updateMyPostList(data: PagingData<Post>)
    suspend fun fetchPostList(): Flow<PagingData<Post>>
    suspend fun fetchMyPostList(): Flow<PagingData<Post>>
    suspend fun uploadPost(content: String): Boolean
    suspend fun getPostWithId(id: String): PostDto?
    suspend fun editPost(id: String, content: String)
    suspend fun deletePost(id: String)
}

@Singleton
class PostUseCaseImpl @Inject constructor(
    private val repository: PostRepository,
    private val errorController: ApiErrorController
): PostUseCase {

    private val _postList = MutableStateFlow<PagingData<Post>>(PagingData.empty())
    override val postList: StateFlow<PagingData<Post>>
        get() = _postList

    private val _myPostList = MutableStateFlow<PagingData<Post>>(PagingData.empty())
    override val myPostList: StateFlow<PagingData<Post>>
        get() = _myPostList

    override suspend fun updatePostList(data: PagingData<Post>) {
        _postList.emit(data)
    }

    override suspend fun updateMyPostList(data: PagingData<Post>) {
        _myPostList.emit(data)
    }

    override suspend fun fetchPostList(): Flow<PagingData<Post>> {
        return try {
            repository.fetchPostList().map { it.map { dto -> Post(dto) } }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            flowOf(PagingData.empty())
        } catch (e: Exception) {
            Log.e("PostUseCase", "Unknown Exception on fetchPostList: ${e.message}")
            flowOf(PagingData.empty())
        }
    }

    override suspend fun fetchMyPostList(): Flow<PagingData<Post>> {
        return try {
            repository.fetchMyPostList().map { it.map { dto -> Post(dto) } }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            flowOf(PagingData.empty())
        } catch (e: Exception) {
            Log.e("PostUseCase", "Unknown Exception on fetchMyPostList: ${e.message}")
            flowOf(PagingData.empty())
        }
    }

    override suspend fun uploadPost(content: String): Boolean {
        val dto = UploadPostDto(content)
        return try {
            val response = repository.uploadPost(dto)
            if (response.isSuccessful) {
                true
            } else {
                errorController.setErrorState(response.code())
                false
            }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            false
        } catch (e: Exception) {
            Log.e("PostUseCase", "Unknown Exception on uploadPost: ${e.message}")
            false
        }
    }

    override suspend fun getPostWithId(id: String): PostDto? {
        return repository.getPostWithId(id)
    }

    override suspend fun editPost(id: String, content: String) {
        repository.editPost(id, content)
    }

    override suspend fun deletePost(id: String) {
        repository.deletePost(id)
    }
}