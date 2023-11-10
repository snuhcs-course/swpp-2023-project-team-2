package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import com.goliath.emojihub.usecases.PostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase
): ViewModel() {
    suspend fun uploadPost(content: String): Boolean {
        return postUseCase.uploadPost(content)
    }

    suspend fun fetchPostList(numLimit: Int) {
        postUseCase.fetchPostList(numLimit)
    }

    suspend fun getPostWithId(id: String) {
        postUseCase.getPostWithId(id)
    }

    suspend fun editPost(id: String, content: String) {
        postUseCase.editPost(id, content)
    }

    suspend fun deletePost(id: String) {
        postUseCase.deletePost(id)
    }
    suspend fun addReaction() {
        postUseCase.addReaction()
    }
}
