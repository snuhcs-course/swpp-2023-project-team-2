package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.goliath.emojihub.usecases.ReactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReactionViewModel @Inject constructor(
    private val reactionUseCase: ReactionUseCase
): ViewModel() {

    val reactionList = reactionUseCase.reactionList

    fun fetchReactionList(postId: String, emojiUnicode: String) {
        viewModelScope.launch {
            reactionUseCase.fetchReactionList(postId, emojiUnicode)
                .cachedIn(viewModelScope)
                .collect {
                    reactionUseCase.updateReactionList(it)
                }
        }
    }

    suspend fun uploadReaction(postId: String, emojiId: String): Boolean {
        return reactionUseCase.uploadReaction(postId, emojiId)
    }

    suspend fun getReactionWithId(id: String) {
        reactionUseCase.getReactionWithId(id)
    }

    suspend fun deleteReaction(reactionId: String) {
        reactionUseCase.deleteReaction(reactionId)
    }
}