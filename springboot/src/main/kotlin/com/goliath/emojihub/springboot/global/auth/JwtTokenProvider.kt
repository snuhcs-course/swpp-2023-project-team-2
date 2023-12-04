package com.goliath.emojihub.springboot.global.auth

import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp401
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.INVALID_TOKEN
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.NO_TOKEN
import com.goliath.emojihub.springboot.global.exception.ErrorType.Unauthorized.EXPIRED_TOKEN
import com.goliath.emojihub.springboot.global.util.StringValue.JWT.JWT_TOKEN_PREFIX
import com.goliath.emojihub.springboot.global.util.StringValue.Header.AUTHORIZATION
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.USERNAME
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService

@Component
@ConfigurationPropertiesScan
class JwtTokenProvider (
    private val authProperties: AuthProperties,
    private val userDetailsService: UserDetailsService,
    ){
    private val signingKey = Keys.hmacShaKeyFor(authProperties.jwtSecret.toByteArray())

    fun createToken(username: String): String {
        val claims: Claims = Jwts.claims()
        claims[USERNAME.string] = username
        val issuer = authProperties.issuer
        val expiryDate: Date = Date.from(
            LocalDateTime
                .now()
                .plusSeconds(authProperties.jwtExpiration)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        )
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUsernameFromToken(authToken: String): String {
        return try {
            getAllClaims(authToken)[USERNAME.string] as String
        } catch (e: ExpiredJwtException) {
            throw CustomHttp401(EXPIRED_TOKEN)
        } catch (e: Exception) {
            throw CustomHttp400(INVALID_TOKEN)
        }
    }

    fun getAllClaims(authToken: String): Claims {
        val prefixRemoved = authToken.replace(JWT_TOKEN_PREFIX.string, "").trim { it <= ' ' }
        return Jwts
            .parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(prefixRemoved)
            .body
    }

    fun resolveToken(request: HttpServletRequest): String {
        return request.getHeader(AUTHORIZATION.string) ?: throw CustomHttp400(NO_TOKEN)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            return getAllClaims(authToken).expiration.after(Date())
        } catch (e: ExpiredJwtException) {
            throw CustomHttp401(EXPIRED_TOKEN)
        } catch (e: Exception) {
            throw CustomHttp400(INVALID_TOKEN)
        }
    }

    fun getAuthentication(authToken: String): Authentication {
        val username = getUsernameFromToken(authToken)
        val userDetails = userDetailsService.loadUserByUsername(username)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

}