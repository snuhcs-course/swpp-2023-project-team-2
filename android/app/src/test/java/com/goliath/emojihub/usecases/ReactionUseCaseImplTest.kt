package com.goliath.emojihub.usecases

import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.testing.asSnapshot
import com.goliath.emojihub.createReactionWithEmojiDtoList
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.models.ReactionWithEmoji
import com.goliath.emojihub.repositories.remote.ReactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ReactionUseCaseImplTest {
    private val reactionRepository = mockk<ReactionRepository>()
    private val apiErrorController = spyk<ApiErrorController>()
    private val reactionUseCase = ReactionUseCaseImpl(reactionRepository, apiErrorController)
    @Before
    fun setUp() {
        mockLogClass()
    }

    @Test
    fun updateReactionList_withSamplePagingReactionData_updatesReactionListStateFlow() {
        // given
        val samplePagingReactionData = mockk<PagingData<ReactionWithEmoji>>()
        // when
        runBlocking { reactionUseCase.updateReactionList(samplePagingReactionData) }
        // then
        assertEquals(samplePagingReactionData, reactionUseCase.reactionList.value)
    }

    @Test
    fun fetchReactionList_returnsFlowOfReactionPagingData() {
        // given
        val sampleReactionPagingDataFlow = createReactionWithEmojiDtoList(5)
        val sampleAnswer = sampleReactionPagingDataFlow.map { it.map { dto -> ReactionWithEmoji(dto) } }
        coEvery {
            reactionRepository.fetchReactionList(any(), any())
        } returns sampleReactionPagingDataFlow
        // when
        val result = runBlocking { reactionUseCase.fetchReactionList("1234", "U+1F44D") }
        // then
        coVerify(exactly = 1) { reactionRepository.fetchReactionList(any(), any()) }
        runBlocking {
            val sampleAnswerAsSnapshot = sampleAnswer.asSnapshot()
            val resultAsSnapshot = result.asSnapshot()
            for (i in sampleAnswerAsSnapshot.indices) {
                assertEquals(sampleAnswerAsSnapshot[i], resultAsSnapshot[i])
            }
        }
    }

    @Test
    fun uploadReaction_successWithValidPostId_returnsTrue() {
        // given
        val samplePostId = "1234"
        val sampleEmojiId = "5678"
        coEvery {
            reactionRepository.uploadReaction(any(), any())
        } returns Response.success(Unit)
        // when
        val result = runBlocking { reactionUseCase.uploadReaction(samplePostId, sampleEmojiId) }
        // then
        assertTrue(result)
    }

    @Test
    fun uploadReaction_failureWithInvalidPostId_returnsFalse() {
        // given
        val samplePostId = "-1234"
        val sampleEmojiId = "5678"
        coEvery {
            reactionRepository.uploadReaction(any(), any())
        } returns Response.error(404, mockk(relaxed = true))
        // when
        val result = runBlocking { reactionUseCase.uploadReaction(samplePostId, sampleEmojiId) }
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