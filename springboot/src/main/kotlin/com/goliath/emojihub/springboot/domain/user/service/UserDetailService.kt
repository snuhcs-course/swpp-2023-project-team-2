package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.model.UserAdapter
import com.goliath.emojihub.springboot.global.common.CustomHttp404
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailService(
    private val userDao: UserDao
    ): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val userDto = userDao.getUser(username) ?: throw CustomHttp404("Username from the token doesn't exist.")
        return UserAdapter(userDto)
    }

}