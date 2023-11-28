package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.UploadEmojiDto
import retrofit2.Response
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.lang.Exception

@RunWith(JUnit4::class)
class EmojiRepositoryImplTest {
    private val emojiApi = mockk<EmojiApi>()
    private val emojiRepositoryImpl = EmojiRepositoryImpl(emojiApi)
    @Before
    fun setUp() {
        mockLogClass()
    }

//    @Test
    fun fetchEmojiList() {
        TODO("Implement after applying pagination")
    }

//    @Test
    fun getEmojiWithId() {
        TODO("Not yet implemented")
    }

    @Test
    fun uploadEmoji_success_returnsTrue() {
        // given
        mockkStatic(File::class)
        val sampleVideoFile = File("sampleVideoFile")
        val sampleUploadEmojiDto = mockk<UploadEmojiDto>()
        coEvery {
            emojiApi.uploadEmoji(any(), any())
        } returns Response.success(Unit)
        // when
        val isUploaded = runBlocking {
            emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
        }
        // then
        coVerify(exactly = 1) { emojiApi.uploadEmoji(any(), any()) }
        assertTrue(isUploaded)
    }

    @Test
    fun uploadEmoji_failureWithIOException_returnsFalse() {
        // given
        mockkStatic(File::class)
        val sampleVideoFile = File("sampleVideoFile")
        val sampleUploadEmojiDto = mockk<UploadEmojiDto>()
        coEvery {
            emojiApi.uploadEmoji(any(), any())
        } throws mockk<IOException>()
        // when
        val isUploaded = runBlocking {
            emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
        }
        // then
        coVerify(exactly = 1) { emojiApi.uploadEmoji(any(), any()) }
        assertFalse(isUploaded)
    }

    @Test
    fun uploadEmoji_failureWithHttpException_returnsFalse() {
        // given
        mockkStatic(File::class)
        val sampleVideoFile = File("sampleVideoFile")
        val sampleUploadEmojiDto = mockk<UploadEmojiDto>()
        coEvery {
            emojiApi.uploadEmoji(any(), any())
        } throws mockk<HttpException>()
        // when
        val isUploaded = runBlocking {
            emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
        }
        // then
        coVerify(exactly = 1) { emojiApi.uploadEmoji(any(), any()) }
        assertFalse(isUploaded)
    }

    @Test
    fun uploadEmoji_failureWithOtherException_returnsFalse() {
        // given
        mockkStatic(File::class)
        val sampleVideoFile = File("sampleVideoFile")
        val sampleUploadEmojiDto = mockk<UploadEmojiDto>()
        coEvery {
            emojiApi.uploadEmoji(any(), any())
        } throws spyk<Exception>()
        // when
        val isUploaded = runBlocking {
            emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
        }
        // then
        coVerify(exactly = 1) { emojiApi.uploadEmoji(any(), any()) }
        assertFalse(isUploaded)
    }

    @Test
    fun saveEmoji_success_returnsSuccessResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        coEvery {
            emojiApi.saveEmoji(any())
        } returns Response.success(Unit)
        // when
        val response = runBlocking { emojiRepositoryImpl.saveEmoji(sampleEmojiId) }
        // then
        coVerify(exactly = 1) { emojiApi.saveEmoji(sampleEmojiId) }
        assert(response.isSuccessful)
    }

    @Test
    fun saveEmoji_failure_returnsFailureResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        coEvery {
            emojiApi.saveEmoji(any())
        } returns Response.error(400, mockk(relaxed=true))
        // when
        val response = runBlocking { emojiRepositoryImpl.saveEmoji(sampleEmojiId) }
        // then
        coVerify(exactly = 1) { emojiApi.saveEmoji(sampleEmojiId) }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun unSaveEmoji_success_returnsSuccessResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        coEvery {
            emojiApi.unSaveEmoji(any())
        } returns Response.success(Unit)
        // when
        val response = runBlocking { emojiRepositoryImpl.unSaveEmoji(sampleEmojiId) }
        // then
        coVerify(exactly = 1) { emojiApi.unSaveEmoji(sampleEmojiId) }
        assert(response.isSuccessful)
    }

    @Test
    fun unSaveEmoji_failure_returnsFailureResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        coEvery {
            emojiApi.unSaveEmoji(any())
        } returns Response.error(400, mockk(relaxed=true))
        // when
        val response = runBlocking { emojiRepositoryImpl.unSaveEmoji(sampleEmojiId) }
        // then
        coVerify(exactly = 1) { emojiApi.unSaveEmoji(sampleEmojiId) }
        assertFalse(response.isSuccessful)
    }

//    @Test
    fun deleteEmoji() {
        TODO("Not yet implemented")
    }
}