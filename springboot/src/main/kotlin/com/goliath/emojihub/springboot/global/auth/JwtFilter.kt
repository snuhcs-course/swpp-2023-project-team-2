package com.goliath.emojihub.springboot.global.auth

import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(private val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {

    private val EXCLUDE_URLS: List<String> = listOf("/api/user/signup", "/api/user/login")

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            if (!shouldExclude(request)) {
                val authToken: String = jwtTokenProvider.resolveToken(request)
                if (jwtTokenProvider.validateToken(authToken)) {
                    val authentication = jwtTokenProvider.getAuthentication(authToken)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: Exception) {
            request.setAttribute("exception", e)
            throw e
        }
        filterChain.doFilter(request, response)
    }

    private fun shouldExclude(request: HttpServletRequest): Boolean {
        return EXCLUDE_URLS.stream().anyMatch { url -> request.requestURI.contains(url) }
    }
}