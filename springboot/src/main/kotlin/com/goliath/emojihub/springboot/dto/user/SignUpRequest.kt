package com.goliath.emojihub.springboot.dto.user

data class SignUpRequest (
    var email: String,
    var username: String,
    var password: String,
)