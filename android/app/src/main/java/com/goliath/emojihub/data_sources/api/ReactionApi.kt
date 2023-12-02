package com.goliath.emojihub.data_sources.api

import com.goliath.emojihub.models.UploadReactionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReactionApi {
    @POST("reaction")
    suspend fun uploadReaction(
        @Body body: UploadReactionDto
    ): Response<Unit>

    @GET("reactions")
    suspend fun fetchReactionList(
    ): Response<Unit>

    @GET("reaction")
    suspend fun getReactionWithId(
        @Path("id") id: String
    ): Response<Unit>

    @DELETE("reaction")
    suspend fun deleteReaction(
        @Path("id") id: String
    ): Response<Unit>
}