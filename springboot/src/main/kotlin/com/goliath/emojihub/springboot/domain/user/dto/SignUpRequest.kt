package com.goliath.emojihub.springboot.domain.user.dto

data class SignUpRequest (
    var email: String,
    var username: String,
    var password: String,
)