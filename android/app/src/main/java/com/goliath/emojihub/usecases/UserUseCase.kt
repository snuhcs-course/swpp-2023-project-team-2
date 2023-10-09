package com.goliath.emojihub.usecases

import com.goliath.emojihub.repositories.remote.UserRepository

sealed interface UserUseCase {
    fun fetchUser(id: String)
}

class UserUseCaseImpl (
    private val repository: UserRepository
): UserUseCase {
    override fun fetchUser(id: String) {
        repository.fetchUser(id)
    }
}