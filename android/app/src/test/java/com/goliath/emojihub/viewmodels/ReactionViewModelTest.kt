package com.goliath.emojihub.viewmodels

import com.goliath.emojihub.createReactionWithEmojiList
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.usecases.ReactionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ReactionViewModelTest {
    private val reactionUseCase = spyk<ReactionUseCase>()
    private val reactionViewModel = ReactionViewModel(reactionUseCase)

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
    fun fetchReactionList_success_updateReactionList() = runTest {
        // given
        val sampleFetchedReactionList = createReactionWithEmojiList(5)
        coEvery {
            reactionUseCase.fetchReactionList(any(), any())
        } returns sampleFetchedReactionList
        // when
        reactionViewModel.fetchReactionList("1234", "U+1F44D")
        advanceUntilIdle()
        // then
        coVerify(exactly = 1) { reactionUseCase.fetchReactionList(any(), any()) }
        coVerify(exactly = 1) { reactionUseCase.updateReactionList(any()) }
    }

    @Test
    fun uploadReaction_success_returnsTrue() {
        // given
        coEvery {
            reactionUseCase.uploadReaction(any(), any())
        } returns true
        // when
        val result = runBlocking {
            reactionViewModel.uploadReaction("1234", "1234")
        }
        // then
        assertTrue(result)
    }

    @Test
    fun uploadReaction_failure_returnsFalse() {
        // given
        coEvery {
            reactionUseCase.uploadReaction(any(), any())
        } returns false
        // when
        val result = runBlocking {
            reactionViewModel.uploadReaction("1234", "1234")
        }
        // then
        assertFalse(result)
    }

    // @Test
    // TODO: Not implemented yet
    fun getReactionWithId() {
    }

    // @Test
    // TODO: Not implemented yet
    fun deleteReaction() {
    }
}