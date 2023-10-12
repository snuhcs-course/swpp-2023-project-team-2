package com.goliath.emojihub.springboot.dto

data class SignUpRequest (
    var id: String,
    var email: String,
    var username: String,
    var password: String,
        )