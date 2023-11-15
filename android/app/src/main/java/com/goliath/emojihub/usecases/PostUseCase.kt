package com.goliath.emojihub.usecases

import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import com.goliath.emojihub.repositories.remote.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface PostUseCase {
    val postState: StateFlow<Post?>
    val postListState: StateFlow<List<PostDto>>
    suspend fun fetchPostList(numLimit: Int)
    suspend fun uploadPost(content: String): Boolean
    suspend fun getPostWithId(id: String)
    suspend fun editPost(id: String, content: String)
    suspend fun deletePost(id: String)
}
class PostUseCaseImpl @Inject constructor(
    private val repository: PostRepository,
    private val errorController: ApiErrorController
): PostUseCase {
    private val _postState = MutableStateFlow<Post?>(null)
    override val postState: StateFlow<Post?>
        get() = _postState

    private val _postListState = MutableStateFlow<List<PostDto>>(emptyList())
    override val postListState: StateFlow<List<PostDto>>
        get() = _postListState.asStateFlow()

    override suspend fun fetchPostList(numLimit: Int) {
        repository.fetchPostList(numLimit)
        try{
            val postList = repository.fetchPostList(numLimit)
            _postListState.emit(postList)
        } catch (e: Exception) {
            errorController.setErrorState(-1)
        }
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

    override suspend fun getPostWithId(id: String) {
        repository.getPostWithId(id)
    }

    override suspend fun editPost(id: String, content: String) {
        repository.editPost(id, content)
    }

    override suspend fun deletePost(id: String) {
        repository.deletePost(id)
    }
}