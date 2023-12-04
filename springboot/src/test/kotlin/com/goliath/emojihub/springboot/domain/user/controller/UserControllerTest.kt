package com.goliath.emojihub.springboot.domain.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.WithCustomUser
import com.goliath.emojihub.springboot.domain.user.dto.LoginRequest
import com.goliath.emojihub.springboot.domain.user.dto.SignUpRequest
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.domain.user.service.UserService
import com.goliath.emojihub.springboot.global.exception.CustomHttp409
import com.goliath.emojihub.springboot.global.exception.ErrorType.Conflict.ID_EXIST
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.DisplayName
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserController::class)
internal class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
) {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var userService: UserService

    companion object {
        private val testDto = TestDto()
        private val userList = testDto.userList
    }

    @Test
    @WithCustomUser
    @DisplayName("유저 데이터들 가져오기 테스트")
    fun getUsers() {
        // given
        given(userService.getUsers()).willReturn(userList)

        // when
        val result = mockMvc.perform(get("/api/user"))

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", equalTo(userList.size)))
            .andExpect(jsonPath("$[0].email").value(userList[0].email))
            .andExpect(jsonPath("$[0].username").value(userList[0].username))
            .andExpect(jsonPath("$[0].password").value(userList[0].password))
        verify(userService, times(1)).getUsers()
    }

    @Test
    @WithCustomUser
    @DisplayName("회원가입 테스트")
    fun signUp() {
        // given
        val accessToken = "test_token"
        val authToken = UserDto.AuthToken(accessToken = accessToken)
        val request = SignUpRequest(
            email = "test_email",
            username = "test_username",
            password = "test_password"
        )
        given(userService.signUp(any(), any(), any())).willReturn(authToken)

        // when
        val result = mockMvc.perform(
            post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )

        // then
        result.andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").value(accessToken))
        verify(userService, times(1)).signUp(request.email, request.username, request.password)
    }

    @Test
    @WithCustomUser
    @DisplayName("로그인 테스트")
    fun login() {
        // given
        val accessToken = "test_token"
        val authToken = UserDto.AuthToken(accessToken = accessToken)
        val request = LoginRequest(
            username = "test_username",
            password = "test_password"
        )
        given(userService.login(any(), any())).willReturn(authToken)

        // when
        val result = mockMvc.perform(
            post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").value(accessToken))
        verify(userService, times(1)).login(request.username, request.password)
    }

    @Test
    @WithCustomUser
    @DisplayName("로그아웃 테스트")
    fun logout() {
        // when
        val result = mockMvc.perform(
            post("/api/user/logout")
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(userService, times(1)).logout()
    }

    @Test
    @WithCustomUser
    @DisplayName("회원탈퇴 테스트")
    fun signOut() {
        // given
        val username = "custom_username"

        // when
        val result = mockMvc.perform(
            delete("/api/user/signout")
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(userService, times(1)).signOut(username)
    }

    @Test
    @WithCustomUser
    @DisplayName("에러 핸들링 테스트1: 커스텀 에러")
    fun errorHandling1() {
        // given
        val exception = CustomHttp409(ID_EXIST)
        val request = SignUpRequest(
            email = "test_email",
            username = "test_username",
            password = "test_password"
        )
        given(userService.signUp(any(), any(), any())).willThrow(exception)

        // when
        val result = mockMvc.perform(
            post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )

        // then
        result.andExpect(status().isConflict)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errorCode").value(exception.status.value()))
            .andExpect(jsonPath("$.detail").value(exception.message))
        verify(userService, times(1)).signUp(request.email, request.username, request.password)
    }

    @Test
    @WithCustomUser
    @DisplayName("에러 핸들링 테스트2: 커스텀 에러")
    fun errorHandling2() {
        // given
        val exception = RuntimeException()
        val request = SignUpRequest(
            email = "test_email",
            username = "test_username",
            password = "test_password"
        )
        given(userService.signUp(any(), any(), any())).willThrow(exception)

        // when
        val result = mockMvc.perform(
            post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )

        // then
        result.andExpect(status().isInternalServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        verify(userService, times(1)).signUp(request.email, request.username, request.password)
    }
}