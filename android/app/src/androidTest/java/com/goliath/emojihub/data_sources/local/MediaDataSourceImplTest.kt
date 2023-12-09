package com.goliath.emojihub.data_sources.local

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MediaDataSourceImplTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val mediaDataSourceImpl = MediaDataSourceImpl(context)

    private fun getSampleVideoUri(videoFileName: String): Uri {
        val x3dDataSourceImpl = X3dDataSourceImpl(context)
        val sampleVideoAbsolutePath = x3dDataSourceImpl.assetFilePath(videoFileName)
        return Uri.fromFile(File(sampleVideoAbsolutePath))
    }

    @Test
    fun loadVideoMediaMetadataRetriever_validVideoUri_returnsMediaMetadataRetriever() {
        // given
        val videoUri = getSampleVideoUri("Hagrid/test_palm_video.mp4")
        // when
        val mediaMetadataRetriever = mediaDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        // then
        assert(mediaMetadataRetriever is MediaMetadataRetriever)
        assertTrue(
            (mediaMetadataRetriever?.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: 0) > 0
        )
    }

    @Test
    fun loadVideoMediaMetadataRetriever_invalidVideoUri_returnsNull() {
        // given
        val videoUri = Uri.EMPTY
        // when
        val mediaMetadataRetriever = mediaDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        // then
        assertNull(mediaMetadataRetriever)
    }

    @Test
    fun extractFrameImagesFromVideo_validMediaMetadataRetriever_returnsBitmapList() {
        // given
        val videoUri = getSampleVideoUri("Hagrid/test_palm_video.mp4")
        val mediaMetadataRetriever = mediaDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        // when
        val bitmapList = mediaDataSourceImpl
            .extractFrameImagesFromVideo(mediaMetadataRetriever!!, 1)
        // then
        assertEquals(1, bitmapList.size)
    }

    @Test
    fun extractFrameImagesFromVideo_errorOnLoadingVideo_returnsEmptyBitmapList() {
        // given
        val videoUri = getSampleVideoUri("Hagrid/test_palm_video.mp4")
        val mediaMetadataRetriever = mediaDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        mockkObject(mediaMetadataRetriever!!)
        every {
            mediaMetadataRetriever.getFrameAtTime(any(), any())
        } throws IOException()
        // when
        val bitmapList = mediaDataSourceImpl
            .extractFrameImagesFromVideo(mediaMetadataRetriever, 1)
        // then
        assertTrue(bitmapList.isEmpty())
    }

    @Test
    fun bitmapToBase64Utf8_withBitmapImage_returnsBase64String() {
        // given
        val videoUri = getSampleVideoUri("Hagrid/test_palm_video.mp4")
        val mediaMetadataRetriever = mediaDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        val bitmapList = mediaDataSourceImpl
            .extractFrameImagesFromVideo(mediaMetadataRetriever!!, 1)
        // when
        val base64String = mediaDataSourceImpl.bitmapToBase64Utf8(bitmapList[0])
        // then
        assertTrue(base64String.isNotEmpty())
    }

    @Test
    fun getJSONObjectFromAssets_zeroShotClassNameToUnicodeFileName_returnsJSONObject() {
        // given
        val classNameToUnicodeFileName = "zero_shot_classname_to_unicode.json"
        // when
        val jsonObject = mediaDataSourceImpl.getJSONObjectFromAssets(classNameToUnicodeFileName)
        // then
        val likeUnicode = jsonObject?.getString("like")
        assertEquals("U+1F44D", likeUnicode)
    }

    @Test
    fun getJSONObjectFromAssets_invalidFileName_returnsNull() {
        // given
        val invalidFileName = "invalid_file_name.json"
        // when
        val jsonObject = mediaDataSourceImpl.getJSONObjectFromAssets(invalidFileName)
        // then
        assertNull(jsonObject)
    }
}