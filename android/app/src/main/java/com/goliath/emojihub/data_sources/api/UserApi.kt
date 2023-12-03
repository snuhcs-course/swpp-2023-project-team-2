package com.goliath.emojihub.data_sources.api

import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.UserDtoList
import com.goliath.emojihub.models.responses.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {
    @GET("user")
    suspend fun fetchUserList(

    ): Response<Array<UserDtoList>>

    @POST("user/signup")
    suspend fun registerUser(
        @Body body: RegisterUserDto
    ): Response<LoginResponseDto>

    @POST("user/login")
    suspend fun login(
        @Body body: LoginUserDto
    ): Response<LoginResponseDto>

    @POST("user/logout")
    suspend fun logout(): Response<Unit>

    @DELETE("user/signout")
    suspend fun signOut(
        @Header("Authorization") authToken: String
    ): Response<Unit>
}