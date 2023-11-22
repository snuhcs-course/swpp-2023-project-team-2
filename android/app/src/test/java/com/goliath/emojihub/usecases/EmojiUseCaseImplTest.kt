package com.goliath.emojihub.usecases

import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.UploadEmojiDto
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
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
import java.io.File

@RunWith(JUnit4::class)
class EmojiUseCaseImplTest {
    private val emojiRepository = mockk<EmojiRepository>()
    private val x3dRepository = mockk<X3dRepository>()
    private val apiErrorController = spyk<ApiErrorController>()
    private val emojiUseCaseImpl = EmojiUseCaseImpl(
        emojiRepository, x3dRepository, apiErrorController
    )
    @Before
    fun setUp() {
        mockLogClass()
    }

//    @Test
    fun fetchEmojiList() {
        TODO("Implement after pagination is implemented")
    }

    @Test
    fun createEmoji_successWithTop3_returnsListOfCreatedEmoji() {
        // given
        val videoUri = mockk<android.net.Uri>()
        val topK = 3
        val sampleTop3CreatedEmojiList = listOf<CreatedEmoji>(
            mockk(), mockk(), mockk()
        )
        every {
            runBlocking {
                x3dRepository.createEmoji(videoUri, topK)
            }
        } returns sampleTop3CreatedEmojiList
        // when
        val createdEmojiList = runBlocking {
            emojiUseCaseImpl.createEmoji(videoUri, topK)
        }
        // then
        verify(exactly = 1) { runBlocking { x3dRepository.createEmoji(videoUri, topK) } }
        assertEquals(sampleTop3CreatedEmojiList, createdEmojiList)
    }

    @Test
    fun createEmoji_failure_returnsEmptyList() {
        // given
        val videoUri = mockk<android.net.Uri>()
        val topK = 3
        every {
            runBlocking {
                x3dRepository.createEmoji(videoUri, topK)
            }
        } returns emptyList()
        // when
        val createdEmojiList = runBlocking {
            emojiUseCaseImpl.createEmoji(videoUri, topK)
        }
        // then
        verify(exactly = 1) { runBlocking { x3dRepository.createEmoji(videoUri, topK) } }
        assertEquals(emptyList<CreatedEmoji>(), createdEmojiList)
    }

    @Test
    fun uploadEmoji_successWithValidEmojiInfo_returnsTrue() {
        // given
        val emojiUnicode = "U+1F600"
        val emojiLabel = "grinning face"
        mockkStatic(File::class)
        val videoFile = File("sample.mp4")
        every {
            runBlocking {
                emojiRepository.uploadEmoji(videoFile, any())
            }
        } returns true
        // when
        val isUploaded = runBlocking {
            emojiUseCaseImpl.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
        }
        // then
        verify(exactly = 1) {
            runBlocking {
                emojiRepository.uploadEmoji(
                    videoFile,
                    UploadEmojiDto(emojiUnicode, emojiLabel)
                )
            }
        }
        assertTrue(isUploaded)
    }

    @Test
    fun saveEmoji_success_returnsTrue() {
        // given
        val sampleId = "sampleId"
        every {
            runBlocking {
                emojiRepository.saveEmoji(sampleId)
            }
        } returns Response.success(Unit)
        // when
        val isSuccess = runBlocking {
            emojiUseCaseImpl.saveEmoji(sampleId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiRepository.saveEmoji(sampleId) } }
        assertTrue(isSuccess)
    }

    @Test
    fun saveEmoji_failure_returnsFalse() {
        // given
        val sampleId = "sampleId"
        every {
            runBlocking {
                emojiRepository.saveEmoji(sampleId)
            }
        } returns Response.error(404, mockk(relaxed=true))
        // when
        val isSuccess = runBlocking {
            emojiUseCaseImpl.saveEmoji(sampleId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiRepository.saveEmoji(sampleId) } }
        verify(exactly = 1) { apiErrorController.setErrorState(404) }
        assertFalse(isSuccess)
    }

    @Test
    fun unSaveEmoji_success_returnsTrue() {
        // given
        val sampleId = "sampleId"
        every {
            runBlocking {
                emojiRepository.unSaveEmoji(sampleId)
            }
        } returns Response.success(Unit)
        // when
        val isSuccess = runBlocking {
            emojiUseCaseImpl.unSaveEmoji(sampleId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiRepository.unSaveEmoji(sampleId) } }
        assertTrue(isSuccess)
    }

    @Test
    fun unSaveEmoji_failure_returnsFalse() {
        // given
        val sampleId = "sampleId"
        every {
            runBlocking {
                emojiRepository.unSaveEmoji(sampleId)
            }
        } returns Response.error(404, mockk(relaxed=true))
        // when
        val isSuccess = runBlocking {
            emojiUseCaseImpl.unSaveEmoji(sampleId)
        }
        // then
        verify(exactly = 1) { runBlocking { emojiRepository.unSaveEmoji(sampleId) } }
        verify(exactly = 1) { apiErrorController.setErrorState(404) }
        assertFalse(isSuccess)
    }
}