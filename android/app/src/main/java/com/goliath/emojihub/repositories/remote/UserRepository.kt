package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.UserApi
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.UserDtoList
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun fetchUserList(): Array<UserDtoList>
    fun fetchUser(name: String)
    suspend fun registerUser(dto: RegisterUserDto)
    suspend fun login(dto: LoginUserDto): Unit?
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

    override suspend fun login(dto: LoginUserDto): Unit? {
        return userApi.login(dto).body()
    }
}