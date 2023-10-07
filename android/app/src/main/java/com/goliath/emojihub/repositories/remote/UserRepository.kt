package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.repositories.data_sources.UserDataSource

sealed interface UserRepository {
    fun fetchUser(id: String)
}

class UserRepositoryImpl (
    private val dataSource: UserDataSource
): UserRepository {
    override fun fetchUser(id: String) {
        TODO("Not yet implemented")
    }
}