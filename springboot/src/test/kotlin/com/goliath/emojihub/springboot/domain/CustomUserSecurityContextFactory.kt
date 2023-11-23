package com.goliath.emojihub.springboot.domain

import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.domain.user.model.UserAdapter
import com.goliath.emojihub.springboot.domain.user.model.UserToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.test.context.support.WithSecurityContextFactory

class CustomUserSecurityContextFactory : WithSecurityContextFactory<WithCustomUser> {
    override fun createSecurityContext(customUserAnnotation: WithCustomUser): SecurityContext {
        val user = UserDto(
            email = customUserAnnotation.email,
            username = customUserAnnotation.username,
            password = customUserAnnotation.password
        )
        val userPrincipal = UserAdapter(user)
        val userToken = UserToken(userPrincipal)

        return SecurityContextImpl().apply { authentication = userToken }
    }
}
