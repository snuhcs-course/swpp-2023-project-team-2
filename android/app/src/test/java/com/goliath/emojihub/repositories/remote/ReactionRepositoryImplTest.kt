package com.goliath.emojihub.repositories.remote

import androidx.paging.testing.asSnapshot
import com.goliath.emojihub.data_sources.api.ReactionApi
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.sampleReactionWithEmojiDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ReactionRepositoryImplTest {
    private val reactionApi = mockk<ReactionApi>()
    private val reactionRepository = ReactionRepositoryImpl(reactionApi)
    @Before
    fun setUp() {
        mockLogClass()
    }

    @Test
    fun fetchReactionList_returnsFlowOfPagingDataOfReactionWithEmojiDto() {
        val numSampleReactions = 10
        val sampleReactionWithEmojiDtoList = List(numSampleReactions) { sampleReactionWithEmojiDto }
        val expectedFetchedReactionWithEmojiDtoList = List(numSampleReactions*2) { sampleReactionWithEmojiDto }
        // *2 because of .asSnapshot() load one more time
        coEvery {
            reactionApi.fetchReactionList(any(), any(), any(), any())
        } returns Response.success(sampleReactionWithEmojiDtoList)
        // when
        val fetchedReactionPagingDataFlow = runBlocking {
            reactionRepository.fetchReactionList("1234", "U+1F44D")
        }
        val fetchedReactionWithEmojiDtoList = runBlocking {
            fetchedReactionPagingDataFlow.asSnapshot()
        }
        // then
        coVerify(exactly = 2) { reactionApi.fetchReactionList(any(), any(), any(), any()) }
        runBlocking {
            assertEquals(expectedFetchedReactionWithEmojiDtoList.size, fetchedReactionWithEmojiDtoList.size)
            assertEquals(expectedFetchedReactionWithEmojiDtoList, fetchedReactionWithEmojiDtoList)
        }
    }

    @Test
    fun uploadReaction_success_returnsSuccessResponse() {
        // given
        val expectedResponse = Response.success(Unit)
        coEvery {
            reactionApi.uploadReaction(any(), any())
        } returns expectedResponse
        // when
        val response = runBlocking {
            reactionRepository.uploadReaction("1234", "1234")
        }
        // then
        coVerify(exactly = 1) { reactionApi.uploadReaction(any(), any()) }
        assertEquals(expectedResponse, response)
    }

    @Test
    fun uploadReaction_failure_returnsFailureResponse() {
        // given
        val expectedResponse = Response.error<Unit>(400, mockk(relaxed = true))
        coEvery {
            reactionApi.uploadReaction(any(), any())
        } returns expectedResponse
        // when
        val response = runBlocking {
            reactionRepository.uploadReaction("1234", "1234")
        }
        // then
        coVerify(exactly = 1) { reactionApi.uploadReaction(any(), any()) }
        assertFalse(response.isSuccessful)
    }

    // @Test
    // TODO: Not implemented yet
    fun getReactionWithId() {
    }

    @Test
    fun deleteReaction_success_returnsSuccessResponse() {
        // given
        val expectedResponse = Response.success(Unit)
        coEvery {
            reactionApi.deleteReaction(any())
        } returns expectedResponse
        // when
        val response = runBlocking {
            reactionRepository.deleteReaction("1234")
        }
        // then
        coVerify(exactly = 1) { reactionApi.deleteReaction(any()) }
        assertEquals(expectedResponse, response)
    }

    @Test
    fun deleteReaction_failure_returnsFailureResponse() {
        // given
        val expectedResponse = Response.error<Unit>(400, mockk(relaxed = true))
        coEvery {
            reactionApi.deleteReaction(any())
        } returns expectedResponse
        // when
        val response = runBlocking {
            reactionRepository.deleteReaction("1234")
        }
        // then
        coVerify(exactly = 1) { reactionApi.deleteReaction(any()) }
        assertFalse(response.isSuccessful)
    }
}