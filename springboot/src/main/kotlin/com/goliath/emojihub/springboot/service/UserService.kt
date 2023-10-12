package com.goliath.emojihub.springboot.service

import com.goliath.emojihub.springboot.dao.UserDao
import com.goliath.emojihub.springboot.dto.UserDto
import org.springframework.stereotype.Service

interface UserService {
    fun getUsers(): List<UserDto>
}

@Service
class UserServiceImpl(private val userDao: UserDao) : UserService {
    override fun getUsers(): List<UserDto> {
        return userDao.getUsers()
    }
}