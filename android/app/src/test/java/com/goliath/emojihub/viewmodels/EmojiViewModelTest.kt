package com.goliath.emojihub.viewmodels

import android.net.Uri
import com.goliath.emojihub.createDeterministicTrendingEmojiList
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.usecases.EmojiUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class EmojiViewModelTest {
    private val emojiUseCase = spyk<EmojiUseCase>()
    private val emojiViewModel = EmojiViewModel(emojiUseCase)

    private val testDispatcher = StandardTestDispatcher()
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockLogClass()
    }
    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchEmojiList_success_updateTrendingEmojiList() = runTest {
        // given
        val sampleFetchedEmojiList = createDeterministicTrendingEmojiList(10)
        coEvery {
            emojiUseCase.fetchEmojiList(0)
        } returns sampleFetchedEmojiList
        // when
        emojiViewModel.fetchEmojiList()
        advanceUntilIdle()
        // then
        coVerify(exactly = 1) { emojiUseCase.fetchEmojiList(0) }
        coVerify(exactly = 1) { emojiUseCase.updateEmojiList(any()) }
    }

    @Test
    fun toggleSortingMode_success_updateLatestEmojiList() = runTest {
        // given
        // for simplicity of testing, we return the same list for both cases
        val sampleFetchedEmojiList = createDeterministicTrendingEmojiList(10)
        coEvery {
            emojiUseCase.fetchEmojiList(1)
        } returns sampleFetchedEmojiList
        // when
        emojiViewModel.sortByDate = 1
        emojiViewModel.fetchEmojiList()
        advanceUntilIdle()
        // then
        assertEquals(1, emojiViewModel.sortByDate)
        coVerify(exactly = 1) { emojiUseCase.fetchEmojiList(1) }
        coVerify(exactly = 1) { emojiUseCase.updateEmojiList(any()) }
    }

    @Test
    fun fetchMyCreatedEmojiList_success_updateMyCreatedEmojiList() = runTest {
        // given
        val sampleFetchedMyCreatedEmojiList = createDeterministicTrendingEmojiList(10)
        coEvery {
            emojiUseCase.fetchMyCreatedEmojiList()
        } returns sampleFetchedMyCreatedEmojiList
        // when
        emojiViewModel.fetchMyCreatedEmojiList()
        advanceUntilIdle()
        // then
        coVerify(exactly = 1) { emojiUseCase.fetchMyCreatedEmojiList() }
        coVerify(exactly = 1) { emojiUseCase.updateMyCreatedEmojiList(any()) }
    }

    @Test
    fun fetchMySavedEmojiList_success_updateMySavedEmojiList() = runTest {
        // given
        val sampleFetchedMySavedEmojiList = createDeterministicTrendingEmojiList(10)
        coEvery {
            emojiUseCase.fetchMySavedEmojiList()
        } returns sampleFetchedMySavedEmojiList
        // when
        emojiViewModel.fetchMySavedEmojiList()
        advanceUntilIdle()
        // then
        coVerify(exactly = 1) { emojiUseCase.fetchMySavedEmojiList() }
        coVerify(exactly = 1) { emojiUseCase.updateMySavedEmojiList(any()) }
    }

    @Test
    fun createEmoji_success_returnsListOfTopKCreatedEmoji() {
        // given
        val videoUri = spyk<Uri>()
        val sampleEmojiList = listOf(
            CreatedEmoji("id1", "unicode1"),
            CreatedEmoji("id2", "unicode2"),
            CreatedEmoji("id3", "unicode3")
        )
        coEvery {
            emojiUseCase.createEmoji(any(), any())
        } returns sampleEmojiList
        // when
        val createdEmojiList: List<CreatedEmoji> = runBlocking {
            emojiViewModel.createEmoji(videoUri)
        }
        // then
        coVerify { emojiUseCase.createEmoji(videoUri, any()) }
        assertEquals(sampleEmojiList, createdEmojiList)
    }

    @Test
    fun createEmoji_failure_returnsEmptyList() {
        // given
        val videoUri = spyk<Uri>()
        coEvery {
            emojiUseCase.createEmoji(any(), any())
        } returns emptyList()
        // when
        val createdEmojiList: List<CreatedEmoji> = runBlocking {
            emojiViewModel.createEmoji(videoUri)
        }
        // then
        coVerify { emojiUseCase.createEmoji(videoUri, any()) }
        assertEquals(emptyList<CreatedEmoji>(), createdEmojiList)
    }

    @Test
    fun uploadEmoji_success_returnsTrue() {
        // given
        val emojiUnicode = "unicode"
        val emojiLabel = "label"
        val videoFile = mockk<File>()
        coEvery {
            emojiUseCase.uploadEmoji(any(), any(), any())
        } returns true
        // when
        val isUploaded = runBlocking {
            emojiViewModel.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
        }
        // then
        coVerify { emojiUseCase.uploadEmoji(emojiUnicode, emojiLabel, videoFile) }
        assertTrue(isUploaded)
    }

    @Test
    fun uploadEmoji_failure_returnsFalse() {
        // given
        val emojiUnicode = "unicode"
        val emojiLabel = "label"
        val videoFile = mockk<File>()
        coEvery {
            emojiUseCase.uploadEmoji(any(), any(), any())
        } returns false
        // when
        val isUploaded = runBlocking {
            emojiViewModel.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
        }
        // then
        coVerify { emojiUseCase.uploadEmoji(emojiUnicode, emojiLabel, videoFile) }
        assertFalse(isUploaded)
    }

    @Test
    fun saveEmoji_success_setSaveEmojiStateSuccess() = runTest {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiUseCase.saveEmoji(any())
        } returns Result.success(Unit)
        // when
        emojiViewModel.saveEmoji(sampleId)
        advanceUntilIdle()
        // then
        coVerify { emojiUseCase.saveEmoji(sampleId) }
        assertTrue(emojiViewModel.saveEmojiState.value?.isSuccess == true)
    }

    @Test
    fun saveEmoji_failure_setSaveEmojiStateFailure() = runTest {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiUseCase.saveEmoji(any())
        } returns Result.failure(Exception("Failed to save Emoji (sampleId: $sampleId), 404"))
        // when
        emojiViewModel.saveEmoji(sampleId)
        advanceUntilIdle()
        // then
        coVerify { emojiUseCase.saveEmoji(sampleId) }
        assertTrue(emojiViewModel.saveEmojiState.value?.isFailure == true)
    }

    @Test
    fun unSaveEmoji_success_setUnSaveEmojiStateFailure() = runTest {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiUseCase.unSaveEmoji(any())
        } returns Result.success(Unit)
        // when
        emojiViewModel.unSaveEmoji(sampleId)
        advanceUntilIdle()
        // then
        coVerify { emojiUseCase.unSaveEmoji(sampleId) }
        assertTrue(emojiViewModel.unSaveEmojiState.value?.isSuccess == true)
    }

    @Test
    fun unSaveEmoji_failure_setUnSaveEmojiStateFailure() = runTest {
        // given
        val sampleId = "sampleId"
        coEvery {
            emojiUseCase.unSaveEmoji(any())
        } returns Result.failure(Exception("Failed to unsave Emoji (sampleId: $sampleId), 404"))
        // when
        emojiViewModel.unSaveEmoji(sampleId)
        advanceUntilIdle()
        // then
        coVerify { emojiUseCase.unSaveEmoji(sampleId) }
        assertTrue(emojiViewModel.unSaveEmojiState.value?.isFailure == true)
    }
}