package com.goliath.emojihub.springboot.domain.user.model

import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserAdapter(private val userDto: UserDto) : UserDetails {

    override fun getAuthorities() = listOf(SimpleGrantedAuthority("USER"))

    override fun getPassword() = userDto.password

    override fun getUsername() = userDto.username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    val email = userDto.email
}