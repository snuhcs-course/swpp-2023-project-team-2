package com.goliath.emojihub.data_sources

import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.UserDtoList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("/user")
    suspend fun fetchUserList(

    ): Response<Array<UserDtoList>>

    @POST("/user/signup")
    suspend fun registerUser(
        @Body body: RegisterUserDto
    ): Response<String>

    @POST("/user/login")
    suspend fun login(
        @Body body: LoginUserDto
    ): Response<Unit>
}