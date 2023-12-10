package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.api.UserApi
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.UserDetailsDto
import com.goliath.emojihub.models.responses.LoginResponseDto
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun fetchUserList(): Array<UserDetailsDto>
    suspend fun fetchUser(id: String)
    suspend fun fetchMyInfo(authToken: String): Response<UserDetailsDto>
    suspend fun registerUser(dto: RegisterUserDto): Response<LoginResponseDto>
    suspend fun login(dto: LoginUserDto): Response<LoginResponseDto>
    suspend fun logout(): Response<Unit>
    suspend fun signOut(authToken: String): Response<Unit>
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
): UserRepository {
    override suspend fun fetchUserList(): Array<UserDetailsDto> {
        return userApi.fetchUserList().body() ?: arrayOf()
    }

    override suspend fun fetchUser(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMyInfo(authToken: String): Response<UserDetailsDto> {
        return userApi.fetchMyInfo(authToken)
    }

    override suspend fun registerUser(dto: RegisterUserDto): Response<LoginResponseDto> {
        return userApi.registerUser(dto)
    }

    override suspend fun login(dto: LoginUserDto): Response<LoginResponseDto> {
        return userApi.login(dto)
    }

    override suspend fun logout(): Response<Unit> {
        return userApi.logout()
    }

    override suspend fun signOut(authToken: String): Response<Unit> {
        return userApi.signOut(authToken)
    }
}
