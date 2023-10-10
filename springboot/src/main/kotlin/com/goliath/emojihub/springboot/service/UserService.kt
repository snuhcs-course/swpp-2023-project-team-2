package com.goliath.emojihub.springboot.service

import com.goliath.emojihub.springboot.model.User

interface UserService {
    fun getUsers(): List<User>
}