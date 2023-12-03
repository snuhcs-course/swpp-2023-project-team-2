package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goliath.emojihub.usecases.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userUseCase: UserUseCase
): ViewModel() {
    val accessTokenState = userUseCase.accessTokenState
    val userState = userUseCase.userState

    suspend fun fetchUser(id: String) {
        userUseCase.fetchUser(id)
    }

    suspend fun login(username: String, password: String) {
        userUseCase.login(username, password)
    }

    suspend fun registerUser(email: String, username: String, password: String): Boolean {
        return userUseCase.registerUser(email, username, password)
    }

    fun logout() {
        viewModelScope.launch {
            userUseCase.logout()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userUseCase.signOut()
        }
    }
}