package com.goliath.emojihub.springboot.global.auth

import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.domain.user.model.UserAdapter
import com.goliath.emojihub.springboot.domain.user.service.UserDetailService
import com.goliath.emojihub.springboot.global.exception.CustomHttp400
import com.goliath.emojihub.springboot.global.exception.CustomHttp401
import com.goliath.emojihub.springboot.global.exception.ErrorType.BadRequest.INVALID_TOKEN
import com.goliath.emojihub.springboot.global.exception.ErrorType.Unauthorized.EXPIRED_TOKEN
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(JwtTokenProvider::class)
@EnableConfigurationProperties(AuthProperties::class)
@TestPropertySource(properties = ["auth.jwt.issuer = TEST_ISSUER", "auth.jwt.jwtSecret = TEST_TEST_TEST_TEST_TEST_SECRET_KEY", "auth.jwt.jwtExpiration = 36000000"])
internal class JwtTokenProviderTest {

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    lateinit var userDetailService: UserDetailService

    companion object {
        private val username = "test_username"
        private val tokenPrefix = "Bearer "
        private val expiredToken = tokenPrefix + "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRlc3RfdXNlcm5hbWUiLCJpc3MiOiJURVNUX0lTU1VFUiIsImV4cCI6MTcwMDg2NTc0NX0.k_CyolIiRKApQEtgPlwZZ-G4rgHmoZwL49civpbZRo0"
        private val invalidToken = tokenPrefix + "invalid_token"
        private val validToken = tokenPrefix + "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRlc3RfdXNlcm5hbWUiLCJpc3MiOiJURVNUX0lTU1VFUiIsImV4cCI6MTczNjg2NzM2MX0.WUbHH_RLHih9uLk9JqpVY8gwcNZmK3uSbS4yCMe-lNg"
    }

    @Test
    @DisplayName("토큰 생성 및 토큰에서 username 추출")
    fun createTokenAndGetUserNameFromToken() {
        // when
        val result = jwtTokenProvider.createToken(username)

        // then
        val authToken = tokenPrefix + result
        val usernameFromToken = jwtTokenProvider.getUsernameFromToken(authToken)
        assertEquals(usernameFromToken, username)
    }

    @Test
    @DisplayName("토큰에서 username 추출 실패")
    fun getUsernameFromTokenFail() {
        // when
        val assertThrows1 = assertThrows(CustomHttp401::class.java) {
            jwtTokenProvider.getUsernameFromToken(expiredToken)
        }
        val assertThrows2 = assertThrows(CustomHttp400::class.java) {
            jwtTokenProvider.getUsernameFromToken(invalidToken)
        }

        // then
        assertAll(
            { assertEquals(assertThrows1.message, EXPIRED_TOKEN.getMessage()) },
            { assertEquals(assertThrows2.message, INVALID_TOKEN.getMessage()) },
        )
    }

    @Test
    fun validateToken() {
        // when
        val assertThrows1 = assertThrows(CustomHttp401::class.java) {
            jwtTokenProvider.validateToken(expiredToken)
        }
        val assertThrows2 = assertThrows(CustomHttp400::class.java) {
            jwtTokenProvider.validateToken(invalidToken)
        }

        // then
        assertAll(
            { assertEquals(assertThrows1.message, EXPIRED_TOKEN.getMessage()) },
            { assertEquals(assertThrows2.message, INVALID_TOKEN.getMessage()) },
        )
    }

    @Test
    fun getAuthentication() {
        // given
        val userDetails = UserAdapter(
            UserDto(
                username = username,
                email = "test_email",
                password = "test_password"
            )
        )
        Mockito.`when`(userDetailService.loadUserByUsername(username)).thenReturn(userDetails)

        // when
        val result = jwtTokenProvider.getAuthentication(validToken)

        // then
        assertAll(
            { assertEquals(result.principal, userDetails) },
            { assertEquals(result.credentials, null) }
        )
    }
}