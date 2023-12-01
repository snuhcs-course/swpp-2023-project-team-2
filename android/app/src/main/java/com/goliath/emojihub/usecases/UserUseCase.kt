package com.goliath.emojihub.usecases

import android.util.Log
import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.ApiErrorController
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

interface UserUseCase {
    val userState: StateFlow<User?>

    suspend fun fetchUserList()
    suspend fun fetchUser(id: String)
    suspend fun registerUser(email: String, name: String, password: String): Boolean
    suspend fun login(name: String, password: String)
    suspend fun logout()
    suspend fun signOut()
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

    override suspend fun registerUser(email: String, name: String, password: String): Boolean {
        val dto = RegisterUserDto(email, name, password)
        val response = repository.registerUser(dto)
        response.let {
            if(it.isSuccessful) return true
            else errorController.setErrorState(it.code())
        }
        return response.isSuccessful
    }

    override suspend fun login(name: String, password: String) {
        val dto = LoginUserDto(name, password)
        val response = repository.login(dto)
        response.let {
            if (it.isSuccessful) {
                val accessToken = it.body()?.accessToken
                _userState.update { User(UserDto(accessToken ?: "", name)) }
                EmojiHubApplication.preferences.accessToken = accessToken
                Log.d("logout", "success ${EmojiHubApplication.preferences.accessToken}")
            } else {
                errorController.setErrorState(it.code())
            }
        }
    }

    override suspend fun logout() {
        val response = repository.logout()
        response.let {
            if (it.isSuccessful) {
                Log.d("logout", "success ${EmojiHubApplication.preferences.accessToken}")
                _userState.update { null }
                EmojiHubApplication.preferences.accessToken = null
            } else {
                Log.d("logout", "fail ${it.code()}")
                errorController.setErrorState(it.code())
            }
        }
    }

    override suspend fun signOut() {
        val accessToken = EmojiHubApplication.preferences.accessToken ?: return
        val response = repository.signOut(accessToken)
        response.let {
            if (it.isSuccessful) {
                _userState.update { null }
                EmojiHubApplication.preferences.accessToken = null
            } else {
                errorController.setErrorState(it.code())
            }
        }
    }
}