package com.goliath.emojihub.usecases

import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.ReactionWithEmoji
import com.goliath.emojihub.repositories.remote.ReactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ReactionUseCase {

    val reactionList: StateFlow<PagingData<ReactionWithEmoji>>
    suspend fun fetchReactionList(postId: String, emojiUnicode: String): Flow<PagingData<ReactionWithEmoji>>
    suspend fun updateReactionList(data: PagingData<ReactionWithEmoji>)
    suspend fun uploadReaction(postId: String, emojiId: String): Boolean
    suspend fun getReactionWithId(id: String)
    suspend fun deleteReaction(reactionId: String)
}

@Singleton
class ReactionUseCaseImpl @Inject constructor(
    private val reactionRepository: ReactionRepository,
    private val errorController: ApiErrorController
): ReactionUseCase {

    private val _reactionList = MutableStateFlow<PagingData<ReactionWithEmoji>>(PagingData.empty())
    override val reactionList: StateFlow<PagingData<ReactionWithEmoji>>
        get() = _reactionList

    override suspend fun updateReactionList(data: PagingData<ReactionWithEmoji>) {
        _reactionList.emit(data)
    }

    override suspend fun fetchReactionList(postId: String, emojiUnicode: String): Flow<PagingData<ReactionWithEmoji>> {
        return reactionRepository.fetchReactionList(postId, emojiUnicode).map { it.map { dto -> ReactionWithEmoji(dto) } }
    }

    override suspend fun uploadReaction(postId: String, emojiId: String): Boolean {
        val response = reactionRepository.uploadReaction(postId, emojiId)
        return if (response.isSuccessful) {
            true
        } else {
            errorController.setErrorState(response.code())
            false
        }
    }

    override suspend fun getReactionWithId(id: String) {
        reactionRepository.getReactionWithId(id)
    }

    override suspend fun deleteReaction(reactionId: String) {
        reactionRepository.deleteReaction(reactionId)
    }
}