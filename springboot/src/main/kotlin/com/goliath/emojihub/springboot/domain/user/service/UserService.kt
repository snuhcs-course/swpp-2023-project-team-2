package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.global.common.CustomHttp401
import com.goliath.emojihub.springboot.global.common.CustomHttp404
import com.goliath.emojihub.springboot.global.common.CustomHttp409
import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.dto.LoginRequest
import com.goliath.emojihub.springboot.domain.user.dto.SignUpRequest
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.auth.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

interface UserService {
    fun getUsers(): List<UserDto>
    fun signUp(signUpRequest: SignUpRequest): UserDto.AuthToken
    fun login(loginRequest: LoginRequest): ResponseEntity<UserDto.AuthToken>
}

@Service
class UserServiceImpl(
    private val userDao: UserDao,
    private val jwtTokenProvider: JwtTokenProvider,
    ) : UserService {
    override fun getUsers(): List<UserDto> {
        return userDao.getUsers()
    }

    override fun signUp(signUpRequest: SignUpRequest): UserDto.AuthToken {
        if (userDao.existUser(signUpRequest.username)) {
            throw CustomHttp409("Id already exists.")
        }
        userDao.insertUser(signUpRequest)
        val authToken = jwtTokenProvider.createToken(signUpRequest.username)
        return UserDto.AuthToken(authToken)
    }

    override fun login(loginRequest: LoginRequest): ResponseEntity<UserDto.AuthToken> {
        val user = userDao.getUser(loginRequest.username) ?: throw CustomHttp404("Id doesn't exist.")
        if (loginRequest.password != user.password) {
            throw CustomHttp401("Password is incorrect.")
        }
        val authToken = jwtTokenProvider.createToken(user.username)
        return ResponseEntity.ok().body(UserDto.AuthToken(authToken))
    }
}