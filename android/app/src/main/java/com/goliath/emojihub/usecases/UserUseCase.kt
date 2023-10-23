package com.goliath.emojihub.usecases

import android.util.Log
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.User
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
    suspend fun registerUser(email: String, name: String, password: String)
    suspend fun login(name: String, password: String)
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

    override suspend fun registerUser(email: String, name: String, password: String) {
        val dto = RegisterUserDto(email, name, password)
        repository.registerUser(dto)
    }

    override suspend fun login(name: String, password: String) {
        val dto = LoginUserDto(name, password)
        val result = repository.login(dto)
        Log.d("login result", result.toString())
    }
}