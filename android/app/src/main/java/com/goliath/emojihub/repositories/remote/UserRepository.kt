package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.api.UserApi
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.UserDtoList
import com.goliath.emojihub.models.responses.LoginResponseDto
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun fetchUserList(): Array<UserDtoList>
    fun fetchUser(id: String)
    suspend fun registerUser(dto: RegisterUserDto): Response<LoginResponseDto>
    suspend fun login(dto: LoginUserDto): Response<LoginResponseDto>
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

    override suspend fun registerUser(dto: RegisterUserDto): Response<LoginResponseDto> {
        return userApi.registerUser(dto)
    }

    override suspend fun login(dto: LoginUserDto): Response<LoginResponseDto> {
        return userApi.login(dto)
    }
}
