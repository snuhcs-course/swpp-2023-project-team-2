package com.goliath.emojihub.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goliath.emojihub.models.User
import com.goliath.emojihub.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) {

    private val _loginState = MutableLiveData<Resource<User>>()
    val loginState: LiveData<Resource<User>> = _loginState

    fun onLoginClicked(username: String, password: String) {
            _loginState.value = loginUseCase.login(username, password)  // Hand
    }
}

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    open class Success<out T>(val data: T) : Resource<T>()
    open class Error(val exception: Throwable) : Resource<Nothing>()
}
