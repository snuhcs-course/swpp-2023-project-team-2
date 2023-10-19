package com.goliath.emojihub.data_sources

import com.goliath.emojihub.models.UserDtoList
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("user")
    suspend fun fetchUserList(

    ): Response<Array<UserDtoList>>
}