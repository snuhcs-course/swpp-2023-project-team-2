package com.goliath.emojihub.usecases

import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.data_sources.LocalStorage
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.responses.LoginResponseDto
import com.goliath.emojihub.repositories.remote.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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

    private val sampleEmail = "sampleEmail"
    private val sampleName = "sampleName"
    private val samplePassword = "samplePassword"
    private val sampleAccessToken = "sampleAccessToken"

    // ! Fake SharedPreferences for testing
    class FakeSharedLocalStorage : LocalStorage {
        private val fakePreference = mutableMapOf<String, String>()
        override var accessToken: String?
            get() = fakePreference.getOrDefault("accessToken", "")
            set(value) = fakePreference.set("accessToken", value!!)
    }

    @Before
    fun setUp() {
        mockLogClass()
        mockkObject(EmojiHubApplication.Companion)
        every { EmojiHubApplication.preferences } returns FakeSharedLocalStorage()
    }

    @Test
    fun registerUser_withValidUserInfo_returnsTrue() {
        // given
        val sampleRegisterUserDto = RegisterUserDto(sampleEmail, sampleName, samplePassword)
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
        coVerify(exactly = 1) { userRepository.registerUser(any()) }
        assertTrue(isSuccessfulRegister)
    }

    @Test
    fun registerUser_withInvalidUserInfo_returnsFalse() {
        // given
        val sampleRegisterUserDto = RegisterUserDto(sampleEmail, sampleName, samplePassword)
        coEvery {
            userRepository.registerUser(any())
        } returns Response.error(CustomError.INTERNAL_SERVER_ERROR.statusCode, mockk(relaxed=true))
        // when
        val isSuccessfulRegister = runBlocking {
            userUseCaseImpl.registerUser(
                sampleRegisterUserDto.email,
                sampleRegisterUserDto.name,
                sampleRegisterUserDto.password
            )
        }
        // then
        coVerify(exactly = 1) { userRepository.registerUser(any()) }
        assertFalse(isSuccessfulRegister)
    }

    @Test
    fun login_withValidUserInfo_updateAccessToken() {
        // given
        coEvery {
            userRepository.login(any())
        } returns Response.success(LoginResponseDto(sampleAccessToken))
        // when
        runBlocking { userUseCaseImpl.login(sampleName, samplePassword) }
        // then
        coVerify(exactly = 1) { userRepository.login(any()) }
        assertEquals(
            sampleAccessToken,
            userUseCaseImpl.userState.value?.accessToken
        )
    }

    @Test
    fun login_withWrongUserName_returnsNotFoundError() {
        // given
        val wrongName = "wrongName"
        coEvery {
            userRepository.login(any())
        } returns Response.error(404, mockk(relaxed=true))
        // when
        runBlocking { userUseCaseImpl.login(wrongName, samplePassword) }
        // then
        coVerify(exactly = 1) { userRepository.login(any()) }
        verify(exactly = 1) { apiErrorController.setErrorState(404) }
    }

    @Test
    fun login_withWrongPassword_returnsUnauthorizedError() {
        // given
        val wrongPassword = "wrongPassword"
        coEvery {
            userRepository.login(any())
        } returns Response.error(401, mockk(relaxed=true))
        // when
        runBlocking { userUseCaseImpl.login(sampleName, wrongPassword) }
        // then
        coVerify(exactly = 1) { userRepository.login(any()) }
        verify(exactly = 1) { apiErrorController.setErrorState(401) }
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