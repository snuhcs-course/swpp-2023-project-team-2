package com.goliath.emojihub.usecases

import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.UploadPostDto
import com.goliath.emojihub.repositories.remote.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface PostUseCase {
    val postState: StateFlow<Post?>
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

    override suspend fun fetchPostList(numLimit: Int) {
        repository.fetchPostList(numLimit)
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

    override suspend fun addReaction() {
        TODO("Not yet implemented")
    }
}