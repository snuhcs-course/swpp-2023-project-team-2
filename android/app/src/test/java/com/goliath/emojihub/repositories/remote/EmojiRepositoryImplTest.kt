package com.goliath.emojihub.repositories.remote

import androidx.paging.testing.asSnapshot
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.data_sources.remote.EmojiDataSource
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.UploadEmojiDto
import retrofit2.Response
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
    private val emojiDataSource = mockk<EmojiDataSource>()
    private val emojiRepositoryImpl = EmojiRepositoryImpl(emojiApi, emojiDataSource)
    private val sampleEmojiDto = EmojiDto(
        createdBy = "channn",
        createdAt = "2023-11-24 14:25:05",
        savedCount = 1600,
        videoLink = "https://storage.googleapis.com/emojihub-e2023.appspot.com/uu_2023-11-24%2014%3A25%3A05.mp4?GoogleAccessId=firebase-adminsdk-zynbm@emojihub-e2023.iam.gserviceaccount.com&Expires=1709443506&Signature=I%2BNRJSZ7nYtmrWs%2Fjv4uVAeW8%2BfHGF6GeV0pZRE4Sp5gCFuXLXBTKpgRBl1j2F%2BSSUStSqvBlktHZofZznGHWtsMYHQ99%2Bv7wcenqZweSWSmzse4s9sKAOkykn7pB9EMnFgax4VqGK4U5ey5HNSCKsjyNa5ZqDH8%2BqF%2FcIjQ3huChDMB2Xw1InaHUve0syvW6uz%2BeooDLo2nkGxdtElsDtomq2cAUMgk7nRNIYciYLGJ%2FsrscW7%2FXfD3rn%2BH3EM9z5S9DHKHWiEmh1xf0wpTtDsXom7p14XnZunnnOxpNO5OMFJi2x1kxZBFVc7U88V19eTmasWxdGV5TZipfN2ZMA%3D%3D",
        thumbnailLink = "https://storage.googleapis.com/emojihub-e2023.appspot.com/uu_2023-11-24%2014%3A25%3A05.jpeg?GoogleAccessId=firebase-adminsdk-zynbm@emojihub-e2023.iam.gserviceaccount.com&Expires=1709443506&Signature=lZK4otdQOXBVKz3EeOEgpSqAH5QE3U6KuTz8bo5RwYQ463i0cBEx44zVPJO3dIP%2B3%2FdKkBbJy%2BzIBogKAKUl5jLyP9FwInOZChspQOuI8zp%2FKivvEZImPnoG2C1UiiwB03tHYq0tWEhgj76BB4SarWRtZY4xRZhuVvuJg9%2FNV%2B5XZ7%2BGGjLbzfjc5rA45iwWQGPfgQN0%2FKJsdTieNb5%2F6%2B5QHW4pq7QLxYAGqvea5X6VY1JcUjXU0iZ%2FfI16L%2F1cFZAMPDPNPxC2bbllFH6vkOdb3qKuvGm0M3Y99GCLTv%2BAiObbBCs13AgmBO1OngrBV4db4zNnjUZOtB0rPRgyFw%3D%3D",
        id = "0ZF0MFHOV7974YTV3SBN",
        label = "love it",
        unicode = "U+2764 U+FE0F"
    )
    @Before
    fun setUp() {
        mockLogClass()
    }

    @Test
    fun fetchEmojiList_returnsFlowOfPagingDataOfEmojiDto() {
        // given
        val numSampleEmojis = 10
        val sampleEmojiDtoList = List(numSampleEmojis) { sampleEmojiDto }
        val expectedFetchedEmojiDtoList = List(numSampleEmojis*2) { sampleEmojiDto }
        // *2 because of .asSnapshot() load one more time
        coEvery {
            emojiApi.fetchEmojiList(any(), any(), any())
        } returns Response.success(sampleEmojiDtoList)
        // when
        val fetchedEmojiPagingDataFlow = runBlocking { emojiRepositoryImpl.fetchEmojiList(1) }
        val fetchedEmojiDtoList = runBlocking { fetchedEmojiPagingDataFlow.asSnapshot() }
        // then
        coVerify(exactly = 2) { emojiApi.fetchEmojiList(any(), any(), any()) }
        runBlocking {
            assertEquals(expectedFetchedEmojiDtoList.size, fetchedEmojiDtoList.size)
            assertEquals(expectedFetchedEmojiDtoList, fetchedEmojiDtoList)
        }
    }

    @Test
    fun fetchMyCreatedEmojiList_returnsFlowOfPagingDataOfEmojiDto() {
        // given
        val numSampleEmojis = 10
        val sampleEmojiDtoList = List(numSampleEmojis) { sampleEmojiDto }
        val expectedFetchedEmojiDtoList = List(numSampleEmojis*2) { sampleEmojiDto }
        // *2 because of .asSnapshot() load one more time
        coEvery {
            emojiApi.fetchMyCreatedEmojiList(any(), any(), any())
        } returns Response.success(sampleEmojiDtoList)
        // when
        val fetchedEmojiPagingDataFlow = runBlocking { emojiRepositoryImpl.fetchMyCreatedEmojiList() }
        val fetchedEmojiDtoList = runBlocking { fetchedEmojiPagingDataFlow.asSnapshot() }
        // then
        coVerify(exactly = 2) { emojiApi.fetchMyCreatedEmojiList(any(), any(), any()) }
        runBlocking {
            assertEquals(expectedFetchedEmojiDtoList.size, fetchedEmojiDtoList.size)
            assertEquals(expectedFetchedEmojiDtoList, fetchedEmojiDtoList)
        }
    }

    @Test
    fun fetchMySavedEmojiList_returnsFlowOfPagingDataOfEmojiDto() {
        // given
        val numSampleEmojis = 10
        val sampleEmojiDtoList = List(numSampleEmojis) { sampleEmojiDto }
        val expectedFetchedEmojiDtoList = List(numSampleEmojis*2) { sampleEmojiDto }
        // *2 because of .asSnapshot() load one more time
        coEvery {
            emojiApi.fetchMySavedEmojiList(any(), any(), any())
        } returns Response.success(sampleEmojiDtoList)
        // when
        val fetchedEmojiPagingDataFlow = runBlocking { emojiRepositoryImpl.fetchMySavedEmojiList() }
        val fetchedEmojiDtoList = runBlocking { fetchedEmojiPagingDataFlow.asSnapshot() }
        // then
        coVerify(exactly = 2) { emojiApi.fetchMySavedEmojiList(any(), any(), any()) }
        runBlocking {
            assertEquals(expectedFetchedEmojiDtoList.size, fetchedEmojiDtoList.size)
            assertEquals(expectedFetchedEmojiDtoList, fetchedEmojiDtoList)
        }
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
            emojiApi.uploadEmoji(any(), any(), any())
        } returns Response.success(Unit)

        val emojiRepositoryImpl = spyk(EmojiRepositoryImpl(emojiApi, emojiDataSource))
        every {
            emojiDataSource.createVideoThumbNail(any())
        } returns File("sampleThumbnailFile")

        // when
        val isUploaded = runBlocking {
            emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
        }
        // then
        coVerify(exactly = 1) { emojiApi.uploadEmoji(any(), any(), any()) }
        assertTrue(isUploaded)
    }

    @Test
    fun uploadEmoji_failureWithException_throwsException() {
        // given
        mockkStatic(File::class)
        val sampleVideoFile = File("sampleVideoFile")
        val sampleUploadEmojiDto = mockk<UploadEmojiDto>()
        coEvery {
            emojiApi.uploadEmoji(any(), any(), any())
        } throws mockk<IOException>()

        val emojiRepositoryImpl = spyk(EmojiRepositoryImpl(emojiApi, emojiDataSource))
        every {
            emojiDataSource.createVideoThumbNail(any())
        } returns File("sampleThumbnailFile")

        // when
        try {
            runBlocking {
                emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
            }
        } catch (e: Exception) {
            // then
            coVerify(exactly = 1) { emojiApi.uploadEmoji(any(), any(), any()) }
            assertTrue(e is IOException)
        }
    }

    @Test
    fun uploadEmoji_failureWithHttpException_returnsFalse() {
        // given
        mockkStatic(File::class)
        val sampleVideoFile = File("sampleVideoFile")
        val sampleUploadEmojiDto = mockk<UploadEmojiDto>()
        coEvery {
            emojiApi.uploadEmoji(any(), any(), any())
        } throws mockk<HttpException>()

        val emojiRepositoryImpl = spyk(EmojiRepositoryImpl(emojiApi, emojiDataSource))
        every {
            emojiDataSource.createVideoThumbNail(any())
        } returns File("sampleThumbnailFile")

        // when
        try {
            runBlocking {
                emojiRepositoryImpl.uploadEmoji(sampleVideoFile, sampleUploadEmojiDto)
            }
        } catch (e: Exception) {
            // then
            coVerify(exactly = 1) { emojiApi.uploadEmoji(any(), any(), any()) }
            assertTrue(e is HttpException)
        }
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
        assertTrue(response.isSuccessful)
    }

    @Test
    fun saveEmoji_failure_returnsFailureResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        coEvery {
            emojiApi.saveEmoji(any())
        } returns Response.error(CustomError.BAD_REQUEST.statusCode, mockk(relaxed=true))
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
        assertTrue(response.isSuccessful)
    }

    @Test
    fun unSaveEmoji_failure_returnsFailureResponseUnit() {
        // given
        val sampleEmojiId = "1234"
        coEvery {
            emojiApi.unSaveEmoji(any())
        } returns Response.error(CustomError.BAD_REQUEST.statusCode, mockk(relaxed=true))
        // when
        val result = runBlocking { emojiRepositoryImpl.unSaveEmoji(sampleEmojiId) }
        // then
        coVerify(exactly = 1) { emojiApi.unSaveEmoji(sampleEmojiId) }
        assertFalse(result.isSuccessful)
    }

//    @Test
    fun deleteEmoji() {
        TODO("Not yet implemented")
    }
}