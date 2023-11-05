package com.goliath.emojihub.data_sources.api

import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @GET("post")
    suspend fun fetchPostList(
        @Query("numLimit") numLimit: Int
    ): Response<List<PostDto>>

    @GET("post")
    suspend fun getPostWithId(
        @Path("id") id: String
    ): Response<PostDto>

    @POST("post")
    suspend fun uploadPost(
        @Body content: UploadPostDto
    ): Response<Unit>

    @PATCH("post")
    suspend fun editPost(
        @Path("id") id: String,
        @Body content: UploadPostDto
    ): Response<Unit>

    @DELETE("post")
    suspend fun deletePost(
        @Path("id") id: String
    ): Response<Unit>
}