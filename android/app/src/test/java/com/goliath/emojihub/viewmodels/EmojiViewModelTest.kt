package com.goliath.emojihub.viewmodels

import android.net.Uri
import com.goliath.emojihub.createDeterministicDummyEmojiList
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
    fun fetchEmojiList_success_updateEmojiList() = runTest {
        // given
        val sampleFetchedEmojiList = createDeterministicDummyEmojiList(10)
        coEvery {
            emojiUseCase.fetchEmojiList()
        } returns sampleFetchedEmojiList
        // when
        emojiViewModel.fetchEmojiList()
        advanceUntilIdle()
        // then
        coVerify(exactly = 1) { emojiUseCase.fetchEmojiList() }
        coVerify(exactly = 1) { emojiUseCase.updateEmojiList(any()) }
    }

    @Test
    fun fetchMyCreatedEmojiList_success_updateMyCreatedEmojiList() = runTest {
        // given
        val sampleFetchedMyCreatedEmojiList = createDeterministicDummyEmojiList(10)
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
        val sampleFetchedMySavedEmojiList = createDeterministicDummyEmojiList(10)
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
    fun saveEmoji_success_returnsUnit() {
        // given
        val id = "sampleId"
        coEvery {
            emojiUseCase.saveEmoji(any())
        } returns true
        // when
        runBlocking { emojiViewModel.saveEmoji(id) }
        // then
        coVerify { emojiUseCase.saveEmoji(id) }
    }

    @Test
    fun unSaveEmoji_success_returnsUnit() {
        // given
        val id = "sampleId"
        coEvery {
            emojiUseCase.unSaveEmoji(any())
        } returns true
        // when
        runBlocking { emojiViewModel.unSaveEmoji(id) }
        // then
        coVerify { emojiUseCase.unSaveEmoji(id) }
    }
}