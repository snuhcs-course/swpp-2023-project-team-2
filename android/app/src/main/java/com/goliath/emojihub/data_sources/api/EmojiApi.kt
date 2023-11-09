package com.goliath.emojihub.data_sources.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface EmojiApi {
    @GET("emoji")
    suspend fun fetchEmojiList(

    )

    @GET("emoji")
    suspend fun getEmojiWithId(

    )
    @Multipart
    @POST("emoji")
    suspend fun uploadEmoji(
        @Part file: MultipartBody.Part,
        @Part("PostEmojiRequest") emojiDto: RequestBody
    ): Response<Unit>

    @POST("emoji")
    suspend fun saveEmoji(

    )

    @POST("emoji")
    suspend fun unSaveEmoji(

    )

    @DELETE("emoji")
    suspend fun deleteEmoji(

    )
}