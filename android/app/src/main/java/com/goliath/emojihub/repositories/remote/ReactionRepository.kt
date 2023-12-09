package com.goliath.emojihub.repositories.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.ReactionPagingSource
import com.goliath.emojihub.data_sources.api.ReactionApi
import com.goliath.emojihub.models.ReactionWithEmojiDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface ReactionRepository {
    suspend fun fetchReactionList(postId: String, emojiUnicode: String): Flow<PagingData<ReactionWithEmojiDto>>
    suspend fun uploadReaction(postId: String, emojiId: String): Response<Unit>
    suspend fun getReactionWithId(id: String)
    suspend fun deleteReaction(reactionId: String): Response<Unit>
}

@Singleton
class ReactionRepositoryImpl @Inject constructor(
    private val reactionApi: ReactionApi
): ReactionRepository {
    override suspend fun fetchReactionList(postId: String, emojiUnicode: String): Flow<PagingData<ReactionWithEmojiDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { ReactionPagingSource(reactionApi, postId, emojiUnicode) }
        ).flow
    }

    override suspend fun uploadReaction(postId: String, emojiId: String): Response<Unit> {
        return reactionApi.uploadReaction(postId, emojiId)
    }

    override suspend fun getReactionWithId(id: String) {
        TODO()
    }

    override suspend fun deleteReaction(reactionId: String): Response<Unit> {
        return reactionApi.deleteReaction(reactionId)
    }
}