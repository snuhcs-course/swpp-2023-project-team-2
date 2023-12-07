package com.goliath.emojihub.springboot.global.config

import com.goliath.emojihub.springboot.global.auth.JwtFilter
import com.goliath.emojihub.springboot.global.auth.JwtTokenProvider
import com.goliath.emojihub.springboot.global.exception.CustomEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
) {
    private val POST_WHITELIST = arrayOf("/api/user/signup", "/api/user/login")
    private val GET_WHITELIST = arrayOf("/api/emoji", "/api/post", "/api/reaction")

    @Bean
    fun ignoringCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().requestMatchers(HttpMethod.POST, *POST_WHITELIST)
            web.ignoring().requestMatchers(HttpMethod.GET, *GET_WHITELIST)
        }
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity.httpBasic { httpBasic -> httpBasic.disable() }.csrf { csrf -> csrf.disable() }
            .sessionManagement { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorizeHttpRequests ->
                authorizeHttpRequests.requestMatchers("/api/**").permitAll()
            }
            .exceptionHandling { exceptionHandling -> exceptionHandling.authenticationEntryPoint(CustomEntryPoint()) }
            .addFilterBefore(JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}