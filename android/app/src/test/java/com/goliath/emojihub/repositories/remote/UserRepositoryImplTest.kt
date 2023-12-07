package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.data_sources.api.UserApi
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.LoginUserDto
import com.goliath.emojihub.models.RegisterUserDto
import com.goliath.emojihub.models.responses.LoginResponseDto
import com.goliath.emojihub.sampleUserDetailsDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class UserRepositoryImplTest {
    private val userApi = mockk<UserApi>()
    private val userRepositoryImpl = UserRepositoryImpl(userApi)
    @Before
    fun setUp() {
        mockLogClass()
    }

    @Deprecated("Will be deprecated")
    fun fetchUserList() {
    }

    // @Test
    // TODO("Not yet implemented")
    fun fetchUser() {
    }

    @Test
    fun fetchMyInfo_withValidToken_returnsResponseSuccessWithUserDetailsDto() {
        // given
        coEvery {
            userApi.fetchMyInfo(any())
        } returns Response.success(sampleUserDetailsDto)
        // when
        val response = runBlocking { userRepositoryImpl.fetchMyInfo("sampleToken") }
        // then
        coVerify(exactly = 1) { userApi.fetchMyInfo("sampleToken") }
        assertEquals(sampleUserDetailsDto, response.body())
    }

    @Test
    fun registerUser_withValidRegisterUserDto_returnsResponseSuccessWithAccessToken() {
        // given
        val sampleRegisterUserDto = RegisterUserDto(
            "sampleEmail", "sampleName", "samplePassword"
        )
        val sampleLoginResponseDto = LoginResponseDto("sampleAccessToken")
        coEvery {
            userApi.registerUser(any())
        } returns Response.success(sampleLoginResponseDto)
        // when
        val response = runBlocking { userRepositoryImpl.registerUser(sampleRegisterUserDto) }
        // then
        coVerify(exactly = 1) { userApi.registerUser(sampleRegisterUserDto) }
        assertEquals(sampleLoginResponseDto, response.body())
    }

    @Test
    fun registerUser_withDuplicateUserName_returnsConflictResponseError() {
        // given
        val sampleRegisterUserDto = RegisterUserDto(
            "sampleEmail", "DuplicateName", "samplePassword"
        )
        coEvery {
            userApi.registerUser(any())
        } returns Response.error(CustomError.CONFLICT.statusCode, mockk(relaxed=true))
        // when
        val response = runBlocking { userRepositoryImpl.registerUser(sampleRegisterUserDto) }
        // then
        coVerify(exactly = 1) { userApi.registerUser(sampleRegisterUserDto) }
        assertFalse(response.isSuccessful)
        assertEquals(409, response.code())
    }

    @Test
    fun login_withValidLoginUserDto_returnsResponseSuccessWithAccessToken() {
        // given
        val loginUserDto = LoginUserDto("sampleName", "samplePassword")
        val sampleLoginResponseDto = LoginResponseDto("sampleAccessToken")
        coEvery {
            userApi.login(any())
        } returns Response.success(sampleLoginResponseDto)
        // when
        val response = runBlocking { userRepositoryImpl.login(loginUserDto) }
        // then
        coVerify(exactly = 1) { userApi.login(any()) }
        assertEquals(sampleLoginResponseDto, response.body())
    }

    @Test
    fun login_withUnknownUserName_returnsNotFoundResponseError() {
        // given
        val loginUserDto = LoginUserDto("UnknownName", "samplePassword")
        coEvery {
            userApi.login(any())
        } returns Response.error(CustomError.NOT_FOUND.statusCode, mockk(relaxed=true))
        // when
        val response = runBlocking { userRepositoryImpl.login(loginUserDto) }
        // then
        coVerify(exactly = 1) { userApi.login(any()) }
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test
    fun login_withWrongPassword_returnsUnauthorizedResponseError() {
        // given
        val loginUserDto = LoginUserDto("sampleName", "WrongPassword")
        coEvery {
            userApi.login(any())
        } returns Response.error(CustomError.UNAUTHORIZED.statusCode, mockk(relaxed=true))
        // when
        val response = runBlocking { userRepositoryImpl.login(loginUserDto) }
        // then
        coVerify(exactly = 1) { userApi.login(any()) }
        assertFalse(response.isSuccessful)
        assertEquals(401, response.code())
    }

    @Test
    fun logout_unit_returnsResponseSuccess() {
        // given
        coEvery {
            userApi.logout()
        } returns Response.success(Unit)
        // when
        val response = runBlocking { userRepositoryImpl.logout() }
        // then
        coVerify(exactly = 1) { userApi.logout() }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun signOut_withValidToken_returnsResponseSuccess() {
        // given
        coEvery {
            userApi.signOut(any())
        } returns Response.success(Unit)
        // when
        val response = runBlocking { userRepositoryImpl.signOut("sampleToken") }
        // then
        coVerify(exactly = 1) { userApi.signOut("sampleToken") }
        assertTrue(response.isSuccessful)
    }
}