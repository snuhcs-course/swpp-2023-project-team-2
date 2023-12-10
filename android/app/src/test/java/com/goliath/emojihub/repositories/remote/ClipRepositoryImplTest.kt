package com.goliath.emojihub.repositories.remote

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.api.ClipApi
import com.goliath.emojihub.data_sources.local.MediaDataSource
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.responses.ClipInferenceResponse
import com.goliath.emojihub.models.responses.ClipInferenceResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ClipRepositoryImplTest {
    private val clipApi = mockk<ClipApi>()
    private val mediaDataSource = mockk<MediaDataSource>()
    private val errorController = spyk<ApiErrorController>()
    private val clipRepositoryImpl = ClipRepositoryImpl(clipApi, mediaDataSource, errorController)

    private val fakeJSONMap = hashMapOf<String, Any?>(
        "like" to "U+1F44D",
        "love it" to "U+2764 U+FE0F",
        "ok" to "U+1F646"
    )
    private val fakeJSONObject = JSONObject(fakeJSONMap)
    @Before
    fun setUp() {
        mockLogClass()
        every {
            mediaDataSource.getJSONObjectFromAssets(any())
        } returns fakeJSONObject
    }

    @Test
    fun extractStringImageListFromVideo_withValidVideoUri_returnsFrameImageList() {
        // given
        val validVideoUri = mockk<Uri>()
        every {
            mediaDataSource.loadVideoMediaMetadataRetriever(any())
        } returns mockk<MediaMetadataRetriever>()
        every {
            mediaDataSource.extractFrameImagesFromVideo(any(), any())
        } returns listOf(mockk<Bitmap>())
        every {
            mediaDataSource.bitmapToBase64Utf8(any())
        } returns "base64String"
        // when
        val frameImageList = clipRepositoryImpl
            .extractStringImageListFromVideo(validVideoUri, 1)
        // then
        verify(exactly = 1) {
            mediaDataSource.loadVideoMediaMetadataRetriever(any())
            mediaDataSource.extractFrameImagesFromVideo(any(), any())
            mediaDataSource.bitmapToBase64Utf8(any())
        }
        assertEquals(1, frameImageList.size)
    }

    @Test
    fun extractStringImageListFromVideo_withInvalidVideoUri_returnsEmptyList() {
        // given
        val invalidVideoUri = mockk<Uri>()
        every {
            mediaDataSource.loadVideoMediaMetadataRetriever(any())
        } returns null
        // when
        val frameImageList = clipRepositoryImpl
            .extractStringImageListFromVideo(invalidVideoUri, 1)
        // then
        verify(exactly = 1) { mediaDataSource.loadVideoMediaMetadataRetriever(any()) }
        verify(exactly = 0) {
            mediaDataSource.extractFrameImagesFromVideo(any(), any())
            mediaDataSource.bitmapToBase64Utf8(any())
        }
        assertTrue(frameImageList.isEmpty())
    }

    @Test
    fun runClipInference_errorOnLoadingClassNameToUnicodeJSONObject_returnsEmptyList() {
        // given
        every {
            mediaDataSource.getJSONObjectFromAssets(any())
        } returns null
        // when
        val clipInferenceResponseList = runBlocking {
            clipRepositoryImpl.runClipInference(listOf("base64String"), 1)
        }
        // then
        verify(exactly = 1) { mediaDataSource.getJSONObjectFromAssets(any()) }
        coVerify(exactly = 0) { clipApi.runClipInference(any()) }
        assertTrue(clipInferenceResponseList.isEmpty())
    }

    @Test
    fun runClipInference_withValidImageList_returnsClipInferenceResponseList() {
        // given
        val validImageList = listOf("base64String", "base64String")
        val successResponseBody = listOf(ClipInferenceResponseDto("like", 0.9))
        coEvery {
            clipApi.runClipInference(any())
        } returns Response.success(successResponseBody)
        // when
        val clipInferenceResponseList = runBlocking {
            clipRepositoryImpl.runClipInference(validImageList, 1)
        }
        // then
//        coVerify(exactly = 1) { clipApi.runClipInference(any()) }
        assertEquals(1, clipInferenceResponseList.size)
        assertEquals("like", clipInferenceResponseList[0].emojiName)
        assertEquals(0.9, clipInferenceResponseList[0].score, 0.0)
    }

    @Test
    fun runClipInference_withFailureResponse_returnsEmptyList() {
        // given
        val validImageList = listOf("base64String", "base64String")
        coEvery {
            clipApi.runClipInference(any())
        } returns Response.error(400, mockk(relaxed = true))
        // when
        val clipInferenceResponseList = runBlocking {
            clipRepositoryImpl.runClipInference(validImageList, 1)
        }
        // then
        verify(exactly = 1) { mediaDataSource.getJSONObjectFromAssets(any()) }
        coVerify(exactly = 2) { clipApi.runClipInference(any()) }
        assertTrue(clipInferenceResponseList.isEmpty())
    }

    @Test
    fun inferenceResultToCreatedEmojiList_withValidInferenceResults_returnsCreatedEmojiList() {
        // given
        val validInferenceResults = listOf(
            ClipInferenceResponse(ClipInferenceResponseDto("like", 0.9)),
        )
        // when
        val createdEmojiList = clipRepositoryImpl
            .inferenceResultToCreatedEmojiList(validInferenceResults)
        // then
        verify(exactly = 1) { mediaDataSource.getJSONObjectFromAssets(any()) }
        assertEquals(1, createdEmojiList.size)
    }

    @Test
    fun inferenceResultToCreatedEmojiList_withUnknownClassNameResult_returnsEmptyList() {
        // given
        val invalidInferenceResults = listOf(
            ClipInferenceResponse(ClipInferenceResponseDto("unknown", 0.9)),
        )
        // when
        val createdEmojiList = clipRepositoryImpl
            .inferenceResultToCreatedEmojiList(invalidInferenceResults)
        // then
        verify(exactly = 1) { mediaDataSource.getJSONObjectFromAssets(any()) }
        assertTrue(createdEmojiList.isEmpty())
    }
}