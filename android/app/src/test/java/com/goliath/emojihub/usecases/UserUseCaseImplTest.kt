package com.goliath.emojihub.usecases

import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.SharedLocalStorage
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.UserDto
import com.goliath.emojihub.models.responses.LoginResponseDto
import com.goliath.emojihub.repositories.remote.UserRepository
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
    fun registerUser() {
    }

    @Test
    fun login_validUserInfo_updateAccessToken() {
        // given
        val sampleName = "sampleName"
        val samplePassword = "samplePassword"
        val sampleAccessToken = "sampleAccessToken"
        coEvery {
            userRepository.login(any())
        } returns Response.success(LoginResponseDto(sampleAccessToken))
        mockkStatic(EmojiHubApplication::class)
        val mockPreferences = mockk<SharedLocalStorage>()
        every {
            EmojiHubApplication.preferences
        } returns mockPreferences
        every {
            mockPreferences.accessToken = any()
        } returns Unit
        // when
        runBlocking {
            userUseCaseImpl.login(sampleName, samplePassword)
        }
        // then
        coVerify { userRepository.login(any()) }
        assertEquals(
            User(UserDto(sampleAccessToken, sampleName)),
            userUseCaseImpl.userState.value
        )
    }

    @Test
    fun logout() {
    }

    @Test
    fun signOut() {
    }
}