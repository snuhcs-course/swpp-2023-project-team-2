package com.goliath.emojihub.usecases

import com.goliath.emojihub.EmojiHubApplication
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.data_sources.LocalStorage
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.UserDetails
import com.goliath.emojihub.models.responses.LoginResponseDto
import com.goliath.emojihub.repositories.remote.UserRepository
import com.goliath.emojihub.sampleUserDetailsDto
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
    private lateinit var userUseCaseImpl : UserUseCaseImpl

    private val sampleEmail = "sampleEmail"
    private val sampleName = "sampleName"
    private val samplePassword = "samplePassword"
    private val sampleAccessToken = "sampleAccessToken"

    // ! Fake SharedPreferences for testing
    class FakeSharedLocalStorage : LocalStorage {
        private val fakePreference = mutableMapOf<String, String?>()
        override var accessToken: String?
            get() = fakePreference.getOrDefault("accessToken", "")
            set(value) = fakePreference.set("accessToken", value)

        override var currentUser: String?
            get() = fakePreference.getOrDefault("currentUser", "")
            set(value) = fakePreference.set("currentUser", value)
    }

    @Before
    fun setUp() {
        mockLogClass()
        mockkObject(EmojiHubApplication.Companion)
        every { EmojiHubApplication.preferences } returns FakeSharedLocalStorage()
        userUseCaseImpl = UserUseCaseImpl(userRepository, apiErrorController)
    }

    @Test
    fun fetchMyInfo_success_updateUserDetailsState() {
        // given
        EmojiHubApplication.preferences.accessToken = sampleAccessToken
        coEvery {
            userRepository.fetchMyInfo(any())
        } returns Response.success(sampleUserDetailsDto)
        // when
        runBlocking { userUseCaseImpl.fetchMyInfo() }
        // then
        coVerify(exactly = 1) { userRepository.fetchMyInfo(any()) }
        assertEquals(
            UserDetails(sampleUserDetailsDto),
            userUseCaseImpl.userDetailsState.value
        )
    }

    @Test
    fun fetchMyInfo_failure_updateErrorState() {
        // given
        EmojiHubApplication.preferences.accessToken = sampleAccessToken
        coEvery {
            userRepository.fetchMyInfo(any())
        } returns Response.error(CustomError.INTERNAL_SERVER_ERROR.statusCode, mockk(relaxed=true))
        // when
        runBlocking { userUseCaseImpl.fetchMyInfo() }
        // then
        coVerify(exactly = 1) { userRepository.fetchMyInfo(any()) }
        verify(exactly = 1) {
            apiErrorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        }
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
            userUseCaseImpl.accessTokenState.value
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

    @Test
    fun logout_success_updateAccessTokenAndUserStateToNull() {
        // given
        coEvery {
            userRepository.logout()
        } returns Response.success(Unit)
        // when
        runBlocking { userUseCaseImpl.logout() }
        // then
        coVerify(exactly = 1) { userRepository.logout() }
        assertEquals(
            null,
            userUseCaseImpl.accessTokenState.value
        )
        assertEquals(
            null,
            userUseCaseImpl.userState.value
        )
    }

    @Test
    fun logout_failure_updateErrorState() {
        // given
        coEvery {
            userRepository.logout()
        } returns Response.error(CustomError.INTERNAL_SERVER_ERROR.statusCode, mockk(relaxed=true))
        // when
        runBlocking { userUseCaseImpl.logout() }
        // then
        coVerify(exactly = 1) { userRepository.logout() }
        verify(exactly = 1) {
            apiErrorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        }
    }

    @Test
    fun signOut_success_updateAccessTokenAndUserStateToNull() {
        // given
        coEvery {
            userRepository.signOut(any())
        } returns Response.success(Unit)
        // when
        runBlocking { userUseCaseImpl.signOut() }
        // then
        coVerify(exactly = 1) { userRepository.signOut(any()) }
        assertEquals(
            null,
            userUseCaseImpl.accessTokenState.value
        )
        assertEquals(
            null,
            userUseCaseImpl.userState.value
        )
    }

    @Test
    fun signOut_failure_updateErrorState() {
        // given
        coEvery {
            userRepository.signOut(any())
        } returns Response.error(CustomError.INTERNAL_SERVER_ERROR.statusCode, mockk(relaxed=true))
        // when
        runBlocking { userUseCaseImpl.signOut() }
        // then
        coVerify(exactly = 1) { userRepository.signOut(any()) }
        verify(exactly = 1) {
            apiErrorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
        }
    }
}