package com.goliath.emojihub.viewmodels

import androidx.lifecycle.ViewModel
import com.goliath.emojihub.usecases.PostUseCase
import com.goliath.emojihub.usecases.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase
): ViewModel() {

}
