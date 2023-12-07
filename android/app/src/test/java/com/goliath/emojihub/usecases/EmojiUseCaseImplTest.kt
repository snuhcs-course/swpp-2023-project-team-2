package com.goliath.emojihub.usecases

import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.testing.asSnapshot
import com.goliath.emojihub.createDeterministicTrendingEmojiDtoList
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.UploadEmojiDto
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.flow.map
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

    @Test
    fun updateEmojiList_withSamplePagingEmojiData_updatesEmojiListStateFlow() {
        // given
        val samplePagingEmojiData = mockk<PagingData<Emoji>>()
        // when
        runBlocking { emojiUseCaseImpl.updateEmojiList(samplePagingEmojiData) }
        // then
        assertEquals(samplePagingEmojiData, emojiUseCaseImpl.emojiList.value)
    }

    @Test
    fun updateMyCreatedEmojiList_withSamplePagingEmojiData_updatesMyCreatedEmojiListStateFlow() {
        // given
        val samplePagingEmojiData = mockk<PagingData<Emoji>>()
        // when
        runBlocking { emojiUseCaseImpl.updateMyCreatedEmojiList(samplePagingEmojiData) }
        // then
        assertEquals(samplePagingEmojiData, emojiUseCaseImpl.myCreatedEmojiList.value)
    }

    @Test
    fun updateMySavedEmojiList_withSamplePagingEmojiData_updatesMySavedEmojiListStateFlow() {
        // given
        val samplePagingEmojiData = mockk<PagingData<Emoji>>()
        // when
        runBlocking { emojiUseCaseImpl.updateMySavedEmojiList(samplePagingEmojiData) }
        // then
        assertEquals(samplePagingEmojiData, emojiUseCaseImpl.mySavedEmojiList.value)
    }

    @Test
    fun fetchEmojiList_returnsFlowOfEmojiPagingData() {
        // given
        val sampleEmojiPagingDataFlow = createDeterministicTrendingEmojiDtoList(5)
        val sampleAnswer = sampleEmojiPagingDataFlow.map { it.map { dto -> Emoji(dto) } }
        coEvery {
            emojiRepository.fetchEmojiList(1)
        } returns sampleEmojiPagingDataFlow
        // when
        val fetchedEmojiPagingDataFlow = runBlocking { emojiUseCaseImpl.fetchEmojiList(1) }
        // then
        coVerify(exactly = 1) { emojiRepository.fetchEmojiList(1) }
        runBlocking {
            val sampleAnswerAsSnapshot = sampleAnswer.asSnapshot()
            val fetchedEmojiPagingDataFlowAsSnapshot = fetchedEmojiPagingDataFlow.asSnapshot()
            for (i in sampleAnswerAsSnapshot.indices) {
                assertEquals(
                    sampleAnswerAsSnapshot[i],
                    fetchedEmojiPagingDataFlowAsSnapshot[i]
                )
            }
        }
    }

    @Test
    fun fetchMyCreatedEmojiList_returnsFlowOfEmojiPagingData() {
        // given
        val sampleEmojiPagingDataFlow = createDeterministicTrendingEmojiDtoList(5)
        val sampleAnswer = sampleEmojiPagingDataFlow.map { it.map { dto -> Emoji(dto) } }
        coEvery {
            emojiRepository.fetchMyCreatedEmojiList()
        } returns sampleEmojiPagingDataFlow
        // when
        val fetchedEmojiPagingDataFlow = runBlocking { emojiUseCaseImpl.fetchMyCreatedEmojiList() }
        // then
        coVerify(exactly = 1) { emojiRepository.fetchMyCreatedEmojiList() }
        runBlocking {
            val sampleAnswerAsSnapshot = sampleAnswer.asSnapshot()
            val fetchedEmojiPagingDataFlowAsSnapshot = fetchedEmojiPagingDataFlow.asSnapshot()
            for (i in sampleAnswerAsSnapshot.indices) {
                assertEquals(
                    sampleAnswerAsSnapshot[i],
                    fetchedEmojiPagingDataFlowAsSnapshot[i]
                )
            }
        }
    }

    @Test
    fun fetchMySavedEmojiList_returnsFlowOfEmojiPagingData() {
        // given
        val sampleEmojiPagingDataFlow = createDeterministicTrendingEmojiDtoList(5)
        val sampleAnswer = sampleEmojiPagingDataFlow.map { it.map { dto -> Emoji(dto) } }
        coEvery {
            emojiRepository.fetchMySavedEmojiList()
        } returns sampleEmojiPagingDataFlow
        // when
        val fetchedEmojiPagingDataFlow = runBlocking { emojiUseCaseImpl.fetchMySavedEmojiList() }
        // then
        coVerify(exactly = 1) { emojiRepository.fetchMySavedEmojiList() }
        runBlocking {
            val sampleAnswerAsSnapshot = sampleAnswer.asSnapshot()
            val fetchedEmojiPagingDataFlowAsSnapshot = fetchedEmojiPagingDataFlow.asSnapshot()
            for (i in sampleAnswerAsSnapshot.indices) {
                assertEquals(
                    sampleAnswerAsSnapshot[i],
                    fetchedEmojiPagingDataFlowAsSnapshot[i]
                )
            }
        }
    }

    @Test
    fun createEmoji_successWithTop3_returnsListOfCreatedEmoji() {
        // given
        val videoUri = mockk<android.net.Uri>()
        val topK = 3
        val sampleTop3CreatedEmojiList = listOf<CreatedEmoji>(
            mockk(), mockk(), mockk()
        )
        coEvery {
            x3dRepository.createEmoji(videoUri, topK)
        } returns sampleTop3CreatedEmojiList
        // when
        val createdEmojiList = runBlocking { emojiUseCaseImpl.createEmoji(videoUri, topK) }
        // then
        coVerify(exactly = 1) { x3dRepository.createEmoji(videoUri, topK) }
        assertEquals(sampleTop3CreatedEmojiList, createdEmojiList)
    }

    @Test
    fun createEmoji_failure_returnsEmptyList() {
        // given
        val videoUri = mockk<android.net.Uri>()
        val topK = 3
        coEvery {
            x3dRepository.createEmoji(videoUri, topK)
        } returns emptyList()
        // when
        val createdEmojiList = runBlocking { emojiUseCaseImpl.createEmoji(videoUri, topK) }
        // then
        coVerify(exactly = 1) { x3dRepository.createEmoji(videoUri, topK) }
        assertEquals(emptyList<CreatedEmoji>(), createdEmojiList)
    }

    @Test
    fun uploadEmoji_successWithValidEmojiInfo_returnsTrue() {
        // given
        val emojiUnicode = "U+1F600"
        val emojiLabel = "grinning face"
        mockkStatic(File::class)
        val videoFile = File("sample.mp4")
        coEvery {
            emojiRepository.uploadEmoji(videoFile, any())
        } returns Response.success(Unit)
        // when
        val isUploaded = runBlocking {
            emojiUseCaseImpl.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
        }
        // then
        coVerify(exactly = 1) {
            emojiRepository.uploadEmoji(
                videoFile,
                UploadEmojiDto(emojiUnicode, emojiLabel)
            )
        }
        assertTrue(isUploaded)
    }

    @Test
    fun saveEmoji_success_returnsTrue() {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiRepository.saveEmoji(sampleId)
        } returns Response.success(Unit)
        // when
        val isSuccess = runBlocking { emojiUseCaseImpl.saveEmoji(sampleId) }
        // then
        coVerify(exactly = 1) { emojiRepository.saveEmoji(sampleId) }
        assertTrue(isSuccess)
    }

    @Test
    fun saveEmoji_failure_returnsFalse() {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiRepository.saveEmoji(sampleId)
        } returns Response.error(404, mockk(relaxed = true))
        // when
        val isSuccess = runBlocking { emojiUseCaseImpl.saveEmoji(sampleId) }
        // then
        coVerify(exactly = 1) { emojiRepository.saveEmoji(sampleId) }
        assertFalse(isSuccess)
    }

    @Test
    fun saveEmoji_exception_returnsFalse() {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiRepository.saveEmoji(sampleId)
        } throws Exception("Failed to save Emoji (Id: $sampleId), 404")
        // when
        val isSuccess = runBlocking { emojiUseCaseImpl.saveEmoji(sampleId) }
        // then
        coVerify(exactly = 1) { emojiRepository.saveEmoji(sampleId) }
        assertFalse(isSuccess)
    }

    @Test
    fun unSaveEmoji_success_returnsTrue() {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiRepository.unSaveEmoji(sampleId)
        } returns Response.success(Unit)
        // when
        val isSuccess = runBlocking { emojiUseCaseImpl.unSaveEmoji(sampleId) }
        // then
        coVerify(exactly = 1) { emojiRepository.unSaveEmoji(sampleId) }
        assertTrue(isSuccess)
    }

    @Test
    fun unSaveEmoji_failure_returnsFalse() {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiRepository.unSaveEmoji(sampleId)
        } returns Response.error(404, mockk(relaxed = true))
        // when
        val isSuccess = runBlocking { emojiUseCaseImpl.unSaveEmoji(sampleId) }
        // then
        coVerify(exactly = 1) { emojiRepository.unSaveEmoji(sampleId) }
        assertFalse(isSuccess)
    }

    @Test
    fun unSaveEmoji_exception_returnsFalse() {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiRepository.unSaveEmoji(sampleId)
        } throws Exception("Failed to unSave Emoji (Id: $sampleId), 404")
        // when
        val isSuccess = runBlocking { emojiUseCaseImpl.unSaveEmoji(sampleId) }
        // then
        coVerify(exactly = 1) { emojiRepository.unSaveEmoji(sampleId) }
        assertFalse(isSuccess)
    }
}