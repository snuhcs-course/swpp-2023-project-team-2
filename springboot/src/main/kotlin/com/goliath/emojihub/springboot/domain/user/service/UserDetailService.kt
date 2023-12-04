package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.model.UserAdapter
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_FROM_TOKEN_NOT_FOUND
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailService(
    private val userDao: UserDao
    ): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val userDto = userDao.getUser(username) ?: throw CustomHttp404(USER_FROM_TOKEN_NOT_FOUND)
        return UserAdapter(userDto)
    }

}