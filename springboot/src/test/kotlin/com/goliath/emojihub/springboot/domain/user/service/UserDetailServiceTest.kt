package com.goliath.emojihub.springboot.domain.user.service

import com.goliath.emojihub.springboot.domain.user.dao.UserDao
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.global.exception.CustomHttp404
import com.goliath.emojihub.springboot.global.exception.ErrorType.NotFound.USER_FROM_TOKEN_NOT_FOUND
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(UserDetailService::class)
internal class UserDetailServiceTest {

    @Autowired
    lateinit var userDetailService: UserDetailService

    @MockBean
    lateinit var userDao: UserDao

    @Test
    @DisplayName("UserDetails 가져오기 실패")
    fun loadUserByUsernameFail() {
        // given
        val username = "test_username"
        Mockito.`when`(userDao.getUser(username)).thenReturn(null)

        // when
        val assertThrows = assertThrows(CustomHttp404::class.java) {
            userDetailService.loadUserByUsername(username)
        }

        // then
        assertEquals(assertThrows.message, USER_FROM_TOKEN_NOT_FOUND.getMessage())
        verify(userDao, times(1)).getUser(username)
    }

    @Test
    @DisplayName("UserDetails 가져오기 성공")
    fun loadUserByUsernameSucceed() {
        // given
        val username = "test_username"
        val user = UserDto(
            email = "test_email",
            username = username,
            password = "test_password"
        )
        Mockito.`when`(userDao.getUser(username)).thenReturn(user)

        // when
        val result = userDetailService.loadUserByUsername(username)

        // then
        assertAll(
            { assertEquals(result.password, user.password) },
            { assertEquals(result.username, user.username ) },
            { assertEquals(result.isAccountNonExpired, true) },
            { assertEquals(result.isAccountNonLocked, true) },
            { assertEquals(result.isCredentialsNonExpired, true) },
            { assertEquals(result.isEnabled, true) }
        )
        verify(userDao, times(1)).getUser(username)
    }
}