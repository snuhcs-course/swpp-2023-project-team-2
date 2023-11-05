package com.goliath.emojihub.usecases

import android.util.Log
import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.UserDto
import com.goliath.emojihub.repositories.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed interface UserUseCase {
    val userState: StateFlow<User?>

    suspend fun fetchUserList()
    suspend fun fetchUser(id: String)
    suspend fun registerUser(email: String, name: String, password: String)
    suspend fun login(name: String, password: String)
    fun logout()
    fun signOut()
}

@Singleton
class UserUseCaseImpl @Inject constructor(
    private val repository: UserRepository,
    private val errorController: ApiErrorController
): UserUseCase {

    private val _userState = MutableStateFlow<User?>(null)
    override val userState: StateFlow<User?>
        get() = _userState

    // TODO: remove
    override suspend fun fetchUserList() {
        val userDtoList = repository.fetchUserList()
        print(userDtoList)
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
        val accessToken = repository.login(dto)
        if (!accessToken.isNullOrEmpty()) {
            Log.d("Login Success: Access token", accessToken)
            _userState.update { User(UserDto(accessToken, name)) }
            EmojiHubApplication.preferences.accessToken = accessToken
        } else {
            errorController.setErrorState(CustomError.BAD_REQUEST)
        }
    }

    override fun logout() {
        _userState.update { null }
    }

    override fun signOut() {
        _userState.update { null }
    }
}