package com.goliath.emojihub.usecases

import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.UserDto
import com.goliath.emojihub.viewmodels.Resource

class LoginUseCase {
    fun login(username: String, password: String): Resource<User>? {
        val dummyUser = User(UserDto(
            userId = "1",
            likedEmojiList = arrayOf(),
            createdEmojiList = arrayOf()
        ))
        return Resource.Success(dummyUser);
    }
}