package com.goliath.emojihub.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.usecases.PostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase
): ViewModel() {

    val postList = postUseCase.postList
    val myPostList = postUseCase.myPostList
    var currentPostId by mutableStateOf("")
    lateinit var currentPost: Post

    suspend fun fetchPostList() {
        viewModelScope.launch {
            postUseCase.fetchPostList()
                .cachedIn(viewModelScope)
                .collect {
                    postUseCase.updatePostList(it)
                }
        }
    }

    suspend fun fetchMyPostList() {
        viewModelScope.launch {
            postUseCase.fetchMyPostList()
                .cachedIn(viewModelScope)
                .collect {
                    postUseCase.updateMyPostList(it)
                }
        }
    }

    suspend fun uploadPost(content: String): Boolean {
        return postUseCase.uploadPost(content)
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
}
