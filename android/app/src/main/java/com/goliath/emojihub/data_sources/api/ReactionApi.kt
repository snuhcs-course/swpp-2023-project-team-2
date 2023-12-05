package com.goliath.emojihub.data_sources.api

import com.goliath.emojihub.models.ReactionWithEmojiDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReactionApi {
    @POST("reaction")
    suspend fun uploadReaction(
        @Query("postId") postId: String,
        @Query("emojiId") emojiId: String
    ): Response<Unit>

    @GET("reactions")
    suspend fun fetchReactionList(
        @Query("postId") postId: String,
        @Query("emojiUnicode") emojiUnicode: String,
        @Query("index") index: Int,
        @Query("count") count: Int
    ): Response<List<ReactionWithEmojiDto>>

    @GET("reaction")
    suspend fun getReactionWithId(
        @Path("id") id: String
    ): Response<Unit>

    @DELETE("reaction")
    suspend fun deleteReaction(
        @Query("reactionId") reactionId: String
    ): Response<Unit>
}