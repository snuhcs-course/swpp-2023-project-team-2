package com.goliath.emojihub.usecases

import android.util.Log
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.repositories.remote.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface PostUseCase {
    val postState: StateFlow<Post?>
    suspend fun fetchPostList()
    suspend fun uploadPost(content: String)
}
class PostUseCaseImpl @Inject constructor(
    private val repository: PostRepository
): PostUseCase {
    private val _postState = MutableStateFlow<Post?>(null)
    override val postState: StateFlow<Post?>
        get() = _postState
    override suspend fun fetchPostList() {

    }
    override suspend fun uploadPost(content: String) {
        Log.d("Post", content)
    }
}