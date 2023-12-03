package com.goliath.emojihub.springboot.global.auth

import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(private val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {


    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authToken: String = jwtTokenProvider.resolveToken(request)
            if (jwtTokenProvider.validateToken(authToken)) {
                val authentication = jwtTokenProvider.getAuthentication(authToken)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            request.setAttribute("exception", e)
            throw e
        }
        filterChain.doFilter(request, response)
    }
}