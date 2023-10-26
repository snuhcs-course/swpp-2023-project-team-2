package com.goliath.emojihub.repositories.remote

import android.util.Log
import com.goliath.emojihub.data_sources.UserApi
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.UserDtoList
import com.goliath.emojihub.models.responses.LoginResponseDto
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun fetchUserList(): Array<UserDtoList>
    fun fetchUser(name: String)
    suspend fun registerUser(dto: RegisterUserDto)
    suspend fun login(dto: LoginUserDto): String?
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
): UserRepository {
    override suspend fun fetchUserList(): Array<UserDtoList> {
        return userApi.fetchUserList().body() ?: arrayOf()
    }

    override fun fetchUser(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun registerUser(dto: RegisterUserDto) {
        userApi.registerUser(dto)
    }

    override suspend fun login(dto: LoginUserDto): String? {
        val result = userApi.login(dto)
        if (result.isSuccessful) {
            val accessToken = result.body()?.accessToken
            Log.d("Login Success", accessToken.toString())
            return accessToken
        } else {
            Log.d("Login Failure", result.raw().toString())
        }
        return null
    }
}