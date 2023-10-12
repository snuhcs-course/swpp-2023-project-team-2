package com.goliath.emojihub.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.dummyUser
import com.goliath.emojihub.usecases.UserUseCase
import kotlinx.coroutines.flow.MutableStateFlow

//class LoginViewModel(
//    private val userUseCase: UserUseCase
//): ViewModel() {
//
//    private val _loginState = MutableStateFlow(Resource.Success(dummyUser))
//    val loginState = _loginState
//
//    fun registerUser(username: String, password: String) {
//        _loginState.value = userUseCase.registerUser(username, password)  // Hand
//    }
//}

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    open class Success<out T>(val data: T) : Resource<T>()
    open class Error(val exception: Throwable) : Resource<Nothing>()
}
