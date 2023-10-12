package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import com.goliath.emojihub.models.User
import com.goliath.emojihub.usecases.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


class UserViewModel (
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _loginState = MutableStateFlow<User?>(null)
    val loginState = _loginState.asStateFlow()

    fun fetchUser(id: String) {
        userUseCase.fetchUser(id)
    }

    fun registerUser(username: String, password: String) {
        _loginState.update { userUseCase.registerUser(username, password) }
    }
}