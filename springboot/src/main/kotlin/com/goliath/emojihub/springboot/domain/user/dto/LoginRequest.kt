package com.goliath.emojihub.springboot.domain.user.dto

data class LoginRequest (
    var username: String,
    var password: String,
)