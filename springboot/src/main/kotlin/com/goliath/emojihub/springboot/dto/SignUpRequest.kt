package com.goliath.emojihub.springboot.dto

data class SignUpRequest (
    var email: String,
    var username: String,
    var password: String,
)