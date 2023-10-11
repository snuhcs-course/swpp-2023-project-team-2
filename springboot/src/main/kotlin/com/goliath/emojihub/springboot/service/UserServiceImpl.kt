package com.goliath.emojihub.springboot.service

import com.goliath.emojihub.springboot.dao.UserDao
import com.goliath.emojihub.springboot.model.User
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userDao: UserDao) : UserService {
    override fun getUsers(): List<User> {
        return userDao.getUsers()
    }
}