package com.goliath.emojihub.repositories.local

import android.net.Uri
import com.goliath.emojihub.data_sources.local.X3dDataSource
import com.goliath.emojihub.sampleX3dInferenceResultListOverScoreThreshold
import com.goliath.emojihub.sampleX3dInferenceResultListUnderScoreThreshold
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class X3dRepositoryImplTest {
    private val x3dDataSource = mockk<X3dDataSource>()
    private val x3dRepositoryImpl = X3dRepositoryImpl(x3dDataSource)


    @Test
    fun loadVideoTensor_withValidVideoUri_returnsVideoTensor() {
        // given
        val validVideoUri = mockk<Uri>()
        every {
            x3dDataSource.loadVideoMediaMetadataRetriever(any())
        } returns mockk()
        every {
            x3dDataSource.extractFrameTensorsFromVideo(any())
        } returns mockk()
        // when
        val videoTensor = x3dRepositoryImpl.loadVideoTensor(validVideoUri)
        // then
        assertNotNull(videoTensor)
        verify(exactly = 1) {
            x3dDataSource.loadVideoMediaMetadataRetriever(any())
            x3dDataSource.extractFrameTensorsFromVideo(any())
        }
    }

    @Test
    fun loadVideoTensor_withInvalidVideoUri_returnsNull() {
        // given
        val invalidVideoUri = mockk<Uri>()
        every {
            x3dDataSource.loadVideoMediaMetadataRetriever(any())
        } returns null
        // when
        val videoTensor = x3dRepositoryImpl.loadVideoTensor(invalidVideoUri)
        // then
        assertNull(videoTensor)
    }

    @Test
    fun predictEmojiClass_withX3dInferenceResultListOverScoreThreshold_returnsCreatedEmojiList() {
        // given
        every {
            x3dDataSource.runInference(any(), any(), any())
        } returns sampleX3dInferenceResultListOverScoreThreshold
        every {
            x3dDataSource.indexToCreatedEmojiList(any(), any(), any())
        } returns listOf()
        // when
        val createdEmojiList = x3dRepositoryImpl.predictEmojiClass(
            mockk(), mockk(), "", "", 3
        )
        // then
        verify(exactly = 1) {
            x3dDataSource.runInference(any(), any(), any())
            x3dDataSource.indexToCreatedEmojiList(any(), any(), any())
        }
    }

    @Test
    fun predictEmojiClass_withX3dInferenceResultListUnderScoreThreshold_returnsDefaultEmoji() {
        // given
        every {
            x3dDataSource.runInference(any(), any(), any())
        } returns sampleX3dInferenceResultListUnderScoreThreshold
        // when
        val createdEmojiList = x3dRepositoryImpl.predictEmojiClass(
            mockk(), mockk(), "", "", 3
        )
        // then
        verify(exactly = 1) { x3dDataSource.runInference(any(), any(), any()) }
        verify(exactly = 0) { x3dDataSource.indexToCreatedEmojiList(any(), any(), any()) }
    }
}