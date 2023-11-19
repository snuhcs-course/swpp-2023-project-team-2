package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.usecases.PostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase
): ViewModel() {
    private val _postList = MutableStateFlow<PagingData<Post>>(PagingData.empty())
    val postList: StateFlow<PagingData<Post>>
        get() = _postList

    init {
        viewModelScope.launch {
            postUseCase.fetchPostList()
                .cachedIn(viewModelScope)
                .collect {
                    _postList.emit(it)
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
