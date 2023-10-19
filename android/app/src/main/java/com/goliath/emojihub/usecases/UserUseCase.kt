package com.goliath.emojihub.usecases

import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.dummyUser
import com.goliath.emojihub.repositories.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed interface UserUseCase {
    val userState: StateFlow<Array<User>>

    suspend fun fetchUserList()
    suspend fun fetchUser(id: String)
    suspend fun registerUser(id: String, password: String): User
}

@Singleton
class UserUseCaseImpl @Inject constructor(
    private val repository: UserRepository
): UserUseCase {

    private val _userState = MutableStateFlow<Array<User>>(arrayOf())
    override val userState: StateFlow<Array<User>>
        get() = _userState

    override suspend fun fetchUserList() {
        val userDtoList = repository.fetchUserList()
        val userList = userDtoList.map { User(it) }.toTypedArray()
        _userState.update { userList }
    }

    override suspend fun fetchUser(id: String) {
        repository.fetchUser(id)
    }

    override suspend fun registerUser(id: String, password: String): User {
        return dummyUser
    }
}