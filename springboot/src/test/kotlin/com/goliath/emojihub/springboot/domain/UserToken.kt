package com.goliath.emojihub.springboot.domain.user.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class UserToken(
    private val userPrincipal: UserDetails
) : AbstractAuthenticationToken(userPrincipal.authorities) {
    override fun getCredentials() = null
    override fun getPrincipal() = userPrincipal
    override fun isAuthenticated() = true
}