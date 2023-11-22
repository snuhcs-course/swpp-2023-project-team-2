package com.goliath.emojihub.repositories.remote

import android.util.Log
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.UploadEmojiDto
import retrofit2.Response
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import java.io.File

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
        every {
            runBlocking {
                emojiApi.uploadEmoji(any(), any())
            }
        } returns Response.success(Unit)
        // when
        val isUploaded = runBlocking {
            emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiApi.uploadEmoji(any(), any()) } }
        assertTrue(isUploaded)
    }

    @Test
    fun uploadEmoji_failureWithHttpException_returnsFalse() {
        // given
        mockkStatic(File::class)
        val sampleVideoFile = File("sampleVideoFile")
        val sampleUploadEmojiDto = mockk<UploadEmojiDto>()
        every {
            runBlocking {
                emojiApi.uploadEmoji(any(), any())
            }
        } throws mockk<HttpException>()
        // when
        val isUploaded = runBlocking {
            emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiApi.uploadEmoji(any(), any()) } }
        assertFalse(isUploaded)
    }

    @Test
    fun saveEmoji_success_returnsSuccessResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        every {
            runBlocking {
                emojiApi.saveEmoji(any())
            }
        } returns Response.success(Unit)
        // when
        val response = runBlocking {
            emojiRepositoryImpl.saveEmoji(sampleEmojiId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiApi.saveEmoji(sampleEmojiId) } }
        assert(response.isSuccessful)
    }

    @Test
    fun saveEmoji_failure_returnsFailureResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        every {
            runBlocking {
                emojiApi.saveEmoji(any())
            }
        } returns Response.error(400, mockk(relaxed=true))
        // when
        val response = runBlocking {
            emojiRepositoryImpl.saveEmoji(sampleEmojiId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiApi.saveEmoji(sampleEmojiId) } }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun unSaveEmoji_success_returnsSuccessResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        every {
            runBlocking {
                emojiApi.unSaveEmoji(any())
            }
        } returns Response.success(Unit)
        // when
        val response = runBlocking {
            emojiRepositoryImpl.unSaveEmoji(sampleEmojiId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiApi.unSaveEmoji(sampleEmojiId) } }
        assert(response.isSuccessful)
    }

    @Test
    fun unSaveEmoji_failure_returnsFailureResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        every {
            runBlocking {
                emojiApi.unSaveEmoji(any())
            }
        } returns Response.error(400, mockk(relaxed=true))
        // when
        val response = runBlocking {
            emojiRepositoryImpl.unSaveEmoji(sampleEmojiId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiApi.unSaveEmoji(sampleEmojiId) } }
        assertFalse(response.isSuccessful)
    }

//    @Test
    fun deleteEmoji() {
        TODO("Not yet implemented")
    }
}