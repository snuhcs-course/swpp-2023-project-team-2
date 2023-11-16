package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import com.goliath.emojihub.usecases.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userUseCase: UserUseCase
): ViewModel() {

    val userState = userUseCase.userState

    suspend fun fetchUserList() {
        userUseCase.fetchUserList()
    }

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
        userUseCase.logout()
    }

    fun signOut() {
        userUseCase.signOut()
    }
}