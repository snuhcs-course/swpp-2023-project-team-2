package com.goliath.emojihub.springboot.global.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("auth.jwt")
data class AuthProperties (
    val issuer: String,
    val jwtSecret: String,
    val jwtExpiration: Long,
    val refreshExpiration: Long,
)