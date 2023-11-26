package com.goliath.emojihub.data_sources.api

import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.FetchEmojiListDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface EmojiApi {
    @GET("emoji")
    suspend fun fetchEmojiList(
        @Query("sortByDate") sortByDate: Int,
        @Query("index") index: Int,
        @Query("count") count: Int
    ): Response<List<EmojiDto>>

    @GET("emoji")
    suspend fun getEmojiWithId(
        @Path("id") id: String
    ): Response<EmojiDto>

    @GET("emoji/me/created")
    suspend fun fetchMyCreatedEmojiList(
        @Query("sortByDate") sortByDate: Int,
        @Query("index") index: Int,
        @Query("count") count: Int
    ): Response<List<EmojiDto>>

    @GET("emoji/me/saved")
    suspend fun fetchMySavedEmojiList(
        @Query("sortByDate") sortByDate: Int,
        @Query("index") index: Int,
        @Query("count") count: Int
    ): Response<List<EmojiDto>>

    @Multipart
    @POST("emoji")
    suspend fun uploadEmoji(
        @Part file: MultipartBody.Part,
        @Part thumbnail: MultipartBody.Part,
        @Part("postEmojiRequest") emojiDto: RequestBody
    ): Response<Unit>

    @POST("emoji/save")
    suspend fun saveEmoji(
        @Query("emojiId") id: String
    ): Response<Unit>

    @POST("emoji/unsave")
    suspend fun unSaveEmoji(
        @Query("emojiId") id: String
    ): Response<Unit>

    @DELETE("emoji")
    suspend fun deleteEmoji(
        @Query("emojiId") id: String
    ): Response<Unit>
}