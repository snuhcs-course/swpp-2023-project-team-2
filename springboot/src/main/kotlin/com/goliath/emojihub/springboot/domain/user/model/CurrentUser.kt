package com.goliath.emojihub.springboot.domain.user.model

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression="username")
annotation class CurrentUser(val required: Boolean = true)
