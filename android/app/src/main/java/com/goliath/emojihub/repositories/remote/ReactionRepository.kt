package com.goliath.emojihub.repositories.remote

import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.api.ReactionApi
import com.goliath.emojihub.models.ReactionDto
import com.goliath.emojihub.models.UploadReactionDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface ReactionRepository {
    suspend fun fetchReactionList(): Flow<PagingData<ReactionDto>>
    suspend fun uploadReaction(dto: UploadReactionDto): Response<Unit>
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

    override suspend fun uploadReaction(dto: UploadReactionDto): Response<Unit> {
        return reactionApi.uploadReaction(dto)
    }

    override suspend fun getReactionWithId(id: String) {
        TODO()
    }

    override suspend fun deleteReaction(id: String) {
        TODO()
    }
}