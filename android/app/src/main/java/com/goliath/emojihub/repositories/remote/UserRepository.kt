package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.repositories.data_sources.UserDataSource
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    fun fetchUser(id: String)
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserDataSource
): UserRepository {
    override fun fetchUser(id: String) {
        TODO("Not yet implemented")
    }
}