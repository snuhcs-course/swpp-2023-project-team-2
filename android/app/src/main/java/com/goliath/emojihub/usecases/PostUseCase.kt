package com.goliath.emojihub.usecases

import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import com.goliath.emojihub.repositories.remote.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

sealed interface PostUseCase {
    val postList: StateFlow<PagingData<Post>>
    suspend fun updatePostList(data: PagingData<Post>)
    suspend fun fetchPostList(): Flow<PagingData<Post>>
    suspend fun uploadPost(content: String): Boolean
    suspend fun getPostWithId(id: String): PostDto?
    suspend fun editPost(id: String, content: String)
    suspend fun deletePost(id: String)
}
class PostUseCaseImpl @Inject constructor(
    private val repository: PostRepository,
    private val errorController: ApiErrorController
): PostUseCase {

    private val _postList = MutableStateFlow<PagingData<Post>>(PagingData.empty())
    override val postList: StateFlow<PagingData<Post>>
        get() = _postList

    override suspend fun updatePostList(data: PagingData<Post>) {
        _postList.emit(data)
    }

    override suspend fun fetchPostList(): Flow<PagingData<Post>> {
        return repository.fetchPostList().map { it.map { dto -> Post(dto) } }
    }

    override suspend fun uploadPost(content: String): Boolean {
        val dto = UploadPostDto(content)
        val response = repository.uploadPost(dto)
        return if (response.isSuccessful) {
            true
        } else {
            errorController.setErrorState(response.code())
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