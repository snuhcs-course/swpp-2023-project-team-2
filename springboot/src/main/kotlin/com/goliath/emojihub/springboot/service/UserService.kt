package com.goliath.emojihub.springboot.service

import com.goliath.emojihub.springboot.common.CustomHttp401
import com.goliath.emojihub.springboot.common.CustomHttp404
import com.goliath.emojihub.springboot.common.CustomHttp409
import com.goliath.emojihub.springboot.dao.UserDao
import com.goliath.emojihub.springboot.dto.user.LoginRequest
import com.goliath.emojihub.springboot.dto.user.SignUpRequest
import com.goliath.emojihub.springboot.dto.user.UserDto
import org.springframework.stereotype.Service

interface UserService {
    fun getUsers(): List<UserDto>
    fun signUp(signUpRequest: SignUpRequest)
    fun login(loginRequest: LoginRequest)
}

@Service
class UserServiceImpl(private val userDao: UserDao) : UserService {
    override fun getUsers(): List<UserDto> {
        return userDao.getUsers()
    }

    override fun signUp(signUpRequest: SignUpRequest) {
        if (userDao.existUser(signUpRequest.username)) {
            throw CustomHttp409("Id already exists.")
        }
        userDao.insertUser(signUpRequest)
    }

    override fun login(loginRequest: LoginRequest) {
        val user = userDao.getUser(loginRequest.username) ?: throw CustomHttp404("Id doesn't exist.")
        if (loginRequest.password != user.password) {
            throw CustomHttp401("Password is incorrect.")
        }
    }
}