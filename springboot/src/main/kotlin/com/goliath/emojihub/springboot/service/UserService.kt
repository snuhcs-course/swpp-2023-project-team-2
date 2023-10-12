package com.goliath.emojihub.springboot.service

import com.goliath.emojihub.springboot.common.CustomHttp409
import com.goliath.emojihub.springboot.dao.UserDao
import com.goliath.emojihub.springboot.dto.SignUpRequest
import com.goliath.emojihub.springboot.dto.UserDto
import org.springframework.stereotype.Service

interface UserService {
    fun getUsers(): List<UserDto>
    fun signUp(signUpRequest: SignUpRequest)
}

@Service
class UserServiceImpl(private val userDao: UserDao) : UserService {
    override fun getUsers(): List<UserDto> {
        return userDao.getUsers()
    }

    override fun signUp(signUpRequest: SignUpRequest) {
        if (userDao.existUser(signUpRequest.id)) {
            throw CustomHttp409("Id already exists.")
        }
        userDao.insertUser(signUpRequest)
    }
}