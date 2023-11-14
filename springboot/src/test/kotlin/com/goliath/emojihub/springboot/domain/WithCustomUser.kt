package com.goliath.emojihub.springboot.domain

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = CustomUserSecurityContextFactory::class)
annotation class WithCustomUser(
    val email: String = "custom_email",
    val username: String = "custom_username",
    val password: String = "custom_password",
    )
