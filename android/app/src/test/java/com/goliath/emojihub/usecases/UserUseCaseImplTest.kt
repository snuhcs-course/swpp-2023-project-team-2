package com.goliath.emojihub.usecases

import android.app.Application
import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.SharedLocalStorage
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.UserDto
import com.goliath.emojihub.models.responses.LoginResponseDto
import com.goliath.emojihub.repositories.remote.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class UserUseCaseImplTest {
    private val userRepository = mockk<UserRepository>()
    private val apiErrorController = spyk<ApiErrorController>()
    private val userUseCaseImpl = UserUseCaseImpl(userRepository, apiErrorController)
    @Before
    fun setUp() {
        mockLogClass()

    }

    @Test
    fun registerUser_withValidUserInfo_returnsTrue() {
        // given
        val sampleRegisterUserDto = RegisterUserDto(
            "sampleEmail",
            "sampleName",
            "samplePassword"
        )
        val sampleAccessToken = "sampleAccessToken"
        coEvery {
            userRepository.registerUser(any())
        } returns Response.success(LoginResponseDto(sampleAccessToken))
        // when
        val isSuccessfulRegister = runBlocking {
            userUseCaseImpl.registerUser(
                sampleRegisterUserDto.email,
                sampleRegisterUserDto.name,
                sampleRegisterUserDto.password
            )
        }
        // then
        coVerify { userRepository.registerUser(any()) }
        assertTrue(isSuccessfulRegister)
    }

    @Test
    fun registerUser_withInvalidUserInfo_returnsFalse() {
        // given
        val sampleRegisterUserDto = RegisterUserDto(
            "sampleEmail",
            "sampleName",
            "samplePassword"
        )
        coEvery {
            userRepository.registerUser(any())
        } returns Response.error(400, mockk(relaxed=true))
        // when
        val isSuccessfulRegister = runBlocking {
            userUseCaseImpl.registerUser(
                sampleRegisterUserDto.email,
                sampleRegisterUserDto.name,
                sampleRegisterUserDto.password
            )
        }
        // then
        coVerify { userRepository.registerUser(any()) }
        assertFalse(isSuccessfulRegister)
    }

    @Test
    fun login_withValidUserInfo_updateAccessToken() {
        // given
        val sampleName = "sampleName"
        val samplePassword = "samplePassword"
        val sampleAccessToken = "sampleAccessToken"
        coEvery {
            userRepository.login(any())
        } returns Response.success(LoginResponseDto(sampleAccessToken))
        // when
        runBlocking { userUseCaseImpl.login(sampleName, samplePassword) }
        // then
        coVerify { userRepository.login(any()) }
        assertEquals(
            User(UserDto(sampleAccessToken, sampleName)),
            userUseCaseImpl.userState.value
        )
    }

    @Test
    fun login_withWrongUserName_returnsNotFoundError() {
        // given
        val sampleName = "wrongName"
        val samplePassword = "samplePassword"
        coEvery {
            userRepository.login(any())
        } returns Response.error(404, mockk(relaxed=true))
        // when
        runBlocking { userUseCaseImpl.login(sampleName, samplePassword) }
        // then
        coVerify { userRepository.login(any()) }
        verify { apiErrorController.setErrorState(404) }
    }

    @Test
    fun login_withWrongPassword_returnsUnauthorizedError() {
        // given
        val sampleName = "sampleName"
        val samplePassword = "wrongPassword"
        coEvery {
            userRepository.login(any())
        } returns Response.error(401, mockk(relaxed=true))
        // when
        runBlocking { userUseCaseImpl.login(sampleName, samplePassword) }
        // then
        coVerify { userRepository.login(any()) }
        verify { apiErrorController.setErrorState(401) }
    }

    // @Test
    // TODO: Not yet implemented
    fun logout() {
    }

    // @Test
    // TODO: Not yet implemented
    fun signOut() {
    }
}