package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import com.goliath.emojihub.usecases.UserUseCase

class UserViewModel (
    private val useCase: UserUseCase
): ViewModel() {
    fun fetchUser(id: String) {
        useCase.fetchUser(id)
    }
}