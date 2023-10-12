package com.goliath.emojihub.usecases

import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.dummyUser
import com.goliath.emojihub.repositories.remote.UserRepository
import com.goliath.emojihub.viewmodels.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface UserUseCase {
    fun fetchUser(id: String)
    fun registerUser(id: String, password: String): User
}

@Singleton
class UserUseCaseImpl @Inject constructor(
    private val repository: UserRepository
): UserUseCase {
    override fun fetchUser(id: String) {
        repository.fetchUser(id)
    }

    override fun registerUser(id: String, password: String): User {
        return dummyUser
    }
}