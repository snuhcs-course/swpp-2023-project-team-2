package com.goliath.emojihub.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.usecases.PostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase
): ViewModel() {
    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList.asStateFlow()

    suspend fun uploadPost(content: String): Boolean {
        return postUseCase.uploadPost(content)
    }

    fun fetchPostList(numLimit: Int) {
        Log.d("VM", "HERE")
        viewModelScope.launch {
            postUseCase.fetchPostList(numLimit)

            val posts = postUseCase.postListState.value.map { dto -> Post(dto) }
            _postList.emit(posts)
        }
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
