package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.UserApi
import com.goliath.emojihub.models.UserDtoList
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun fetchUserList(): Array<UserDtoList>
    fun fetchUser(name: String)
    fun registerUser(email: String, name: String, password: String)
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

    override fun registerUser(email: String, name: String, password: String) {
        TODO("Not yet implemented")
    }
}