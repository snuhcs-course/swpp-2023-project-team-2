package com.goliath.emojihub.repositories.remote

import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.api.ReactionApi
import com.goliath.emojihub.models.ReactionDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface ReactionRepository {
    suspend fun fetchReactionList(): Flow<PagingData<ReactionDto>>
    suspend fun uploadReaction(postId: String, emojiId: String): Response<Unit>
    suspend fun getReactionWithId(id: String)
    suspend fun deleteReaction(id: String)
}

@Singleton
class ReactionRepositoryImpl @Inject constructor(
    private val reactionApi: ReactionApi
): ReactionRepository {
    override suspend fun fetchReactionList(): Flow<PagingData<ReactionDto>> {
        TODO()
    }

    override suspend fun uploadReaction(postId: String, emojiId: String): Response<Unit> {
        return reactionApi.uploadReaction(postId, emojiId)
    }

    override suspend fun getReactionWithId(id: String) {
        TODO()
    }

    override suspend fun deleteReaction(id: String) {
        TODO()
    }
}