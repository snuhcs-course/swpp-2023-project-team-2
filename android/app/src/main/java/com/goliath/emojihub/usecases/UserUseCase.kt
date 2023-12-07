package com.goliath.emojihub.usecases

import android.util.Log
import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.UserDetails
import com.goliath.emojihub.models.UserDto
import com.goliath.emojihub.repositories.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

sealed interface UserUseCase {
    val accessTokenState: StateFlow<String?>
    val userState: StateFlow<User?>
    val userDetailsState: StateFlow<UserDetails?>

    suspend fun fetchUser(id: String)
    suspend fun fetchMyInfo()
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

    private val _accessTokenState: MutableStateFlow<String?> = MutableStateFlow(EmojiHubApplication.preferences.accessToken)
    override val accessTokenState: StateFlow<String?>
        get() = _accessTokenState

    private val _userState: MutableStateFlow<User?> = MutableStateFlow(User(UserDto(EmojiHubApplication.preferences.currentUser ?: "")))
    override val userState: StateFlow<User?>
        get() = _userState

    private val _userDetailsState: MutableStateFlow<UserDetails?> = MutableStateFlow(null)
    override val userDetailsState: StateFlow<UserDetails?>
        get() = _userDetailsState

    override suspend fun fetchUser(id: String) {
        repository.fetchUser(id)
    }

    override suspend fun fetchMyInfo() {
        val accessToken = EmojiHubApplication.preferences.accessToken ?: return
        if (accessToken.isEmpty()) return
        try {
            val response = repository.fetchMyInfo(accessToken)
            response.let {
                if(it.isSuccessful) {
                    val userDetailsDto = it.body() ?: return // FIXME: may be considered as an error
                    _userDetailsState.update { UserDetails(userDetailsDto) }
                }
                else errorController.setErrorState(it.code())
            }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        } catch (e: Exception) {
            Log.e("UserUseCase", "Unknown Exception on fetchMyEmoji: ${e.message}")
        }
    }

    override suspend fun registerUser(email: String, name: String, password: String): Boolean {
        val dto = RegisterUserDto(email, name, password)
        try {
            val response = repository.registerUser(dto)
            response.let {
                if(it.isSuccessful) return true
                else errorController.setErrorState(it.code())
            }
            return response.isSuccessful
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        } catch (e: Exception) {
            Log.e("UserUseCase", "Unknown Exception on registerUser: ${e.message}")
        }
        return false
    }

    override suspend fun login(name: String, password: String) {
        val dto = LoginUserDto(name, password)
        try {
            val response = repository.login(dto)
            response.let {
                if (it.isSuccessful) {
                    val accessToken = it.body()?.accessToken
                    _accessTokenState.update { accessToken }
                    _userState.update { User(UserDto(name)) }
                    EmojiHubApplication.preferences.accessToken = accessToken
                    EmojiHubApplication.preferences.currentUser = name
                } else {
                    errorController.setErrorState(it.code())
                }
            }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        } catch (e: Exception) {
            Log.e("UserUseCase", "Unknown Exception on login: ${e.message}")
        }
    }

    override suspend fun logout() {
        try {
            val response = repository.logout()
            response.let {
                if (it.isSuccessful) {
                    EmojiHubApplication.preferences.accessToken = null
                    EmojiHubApplication.preferences.currentUser = null
                    _accessTokenState.update { null }
                    _userState.update { null }
                } else {
                    errorController.setErrorState(it.code())
                }
            }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        } catch (e: Exception) {
            Log.e("UserUseCase", "Unknown Exception on logout: ${e.message}")
        }
    }

    override suspend fun signOut() {
        val accessToken = EmojiHubApplication.preferences.accessToken ?: return
        try {
            val response = repository.signOut(accessToken)
            response.let {
                if (it.isSuccessful) {
                    EmojiHubApplication.preferences.accessToken = null
                    EmojiHubApplication.preferences.currentUser = null
                    _accessTokenState.update { null }
                    _userState.update { null }
                } else {
                    errorController.setErrorState(it.code())
                }
            }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        } catch (e: Exception) {
            Log.e("UserUseCase", "Unknown Exception on signOut: ${e.message}")
        }
    }
}