package com.goliath.emojihub.usecases

import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.testing.asSnapshot
import com.goliath.emojihub.createDeterministicDummyPostDtoList
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.UploadPostDto
import com.goliath.emojihub.repositories.remote.PostRepository
import com.goliath.emojihub.samplePostDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class PostUseCaseImplTest {
    private val postRepository = mockk<PostRepository>()
    private val apiErrorController = spyk<ApiErrorController>()
    private val postUseCaseImpl = PostUseCaseImpl(postRepository, apiErrorController)

    @Test
    fun updatePostList_withSamplePagingPostData_updatesPostListStateFlow() {
        // given
        val samplePagingPostData = mockk<PagingData<Post>>()
        // when
        runBlocking { postUseCaseImpl.updatePostList(samplePagingPostData) }
        // then
        assertEquals(samplePagingPostData, postUseCaseImpl.postList.value)
    }

    @Test
    fun updateMyPostList_withSamplePagingPostData_updatesMyPostListStateFlow() {
        // given
        val samplePagingPostData = mockk<PagingData<Post>>()
        // when
        runBlocking { postUseCaseImpl.updateMyPostList(samplePagingPostData) }
        // then
        assertEquals(samplePagingPostData, postUseCaseImpl.myPostList.value)
    }

    @Test
    fun fetchPostList_returnsFlowOfPostPagingData() {
        // given
        val samplePostPagingDataFlow = createDeterministicDummyPostDtoList(5)
        val sampleAnswer = samplePostPagingDataFlow.map { it.map { dto -> Post(dto) } }
        coEvery {
            postRepository.fetchPostList()
        } returns samplePostPagingDataFlow
        // when
        val fetchedPostPagingDataFlow = runBlocking { postUseCaseImpl.fetchPostList() }
        // then
        coVerify(exactly = 1) { postRepository.fetchPostList() }
        runBlocking {
            val sampleAnswerAsSnapshot = sampleAnswer.asSnapshot()
            val fetchedPostPagingDataFlowAsSnapshot = fetchedPostPagingDataFlow.asSnapshot()
            for (i in sampleAnswerAsSnapshot.indices) {
                assertEquals(
                    sampleAnswerAsSnapshot[i],
                    fetchedPostPagingDataFlowAsSnapshot[i]
                )
            }
        }
    }

    @Test
    fun fetchMyPostList_returnsFlowOfPostPagingData() {
        // given
        val samplePostPagingDataFlow = createDeterministicDummyPostDtoList(5)
        val sampleAnswer = samplePostPagingDataFlow.map { it.map { dto -> Post(dto) } }
        coEvery {
            postRepository.fetchMyPostList()
        } returns samplePostPagingDataFlow
        // when
        val fetchedPostPagingDataFlow = runBlocking { postUseCaseImpl.fetchMyPostList() }
        // then
        coVerify(exactly = 1) { postRepository.fetchMyPostList() }
        runBlocking {
            val sampleAnswerAsSnapshot = sampleAnswer.asSnapshot()
            val fetchedPostPagingDataFlowAsSnapshot = fetchedPostPagingDataFlow.asSnapshot()
            for (i in sampleAnswerAsSnapshot.indices) {
                assertEquals(
                    sampleAnswerAsSnapshot[i],
                    fetchedPostPagingDataFlowAsSnapshot[i]
                )
            }
        }
    }

    @Test
    fun uploadPost_success_returnsTrue() {
        // given
        val sampleContent = "sample content"
        coEvery {
            postRepository.uploadPost(any())
        } returns Response.success(mockk())
        // when
        val isSuccess = runBlocking { postUseCaseImpl.uploadPost(sampleContent) }
        // then
        coVerify(exactly = 1) { postRepository.uploadPost(UploadPostDto(sampleContent)) }
        assertTrue(isSuccess)
    }

    @Test
    fun uploadPost_failure_returnsFalse() {
        // given
        val sampleContent = "sample content"
        val sampleErrorCode = 403
        coEvery {
            postRepository.uploadPost(any())
        } returns Response.error(sampleErrorCode, mockk(relaxed = true))
        // when
        val isSuccess = runBlocking { postUseCaseImpl.uploadPost(sampleContent) }
        // then
        coVerify(exactly = 1) { postRepository.uploadPost(UploadPostDto(sampleContent)) }
        verify(exactly = 1) { apiErrorController.setErrorState(sampleErrorCode) }
        assertFalse(isSuccess)
    }

     @Test
    fun getPostWithId_success_returnsPostDto() {
        // given
        val sampleId = "sampleId"
        coEvery {
            postRepository.getPostWithId(any())
        } returns samplePostDto
        // when
        val postDto = runBlocking { postUseCaseImpl.getPostWithId(sampleId) }
        // then
        coVerify(exactly = 1) { postRepository.getPostWithId(sampleId) }
        assertEquals(samplePostDto, postDto)
    }

    @Test
    fun getPostWithId_failure_returnsNull() {
        // given
        val sampleId = "sampleId"
        coEvery {
            postRepository.getPostWithId(any())
        } returns null
        // when
        val postDto = runBlocking { postUseCaseImpl.getPostWithId(sampleId) }
        // then
        coVerify(exactly = 1) { postRepository.getPostWithId(sampleId) }
        assertNull(postDto)
    }

     @Test
    // No return value
    // TODO: NOT FULLY IMPLEMENTED
    fun editPost_verifyRunExactlyOnce() {
        // given
        val sampleId = "sampleId"
        val sampleContent = "sampleContent"
        coEvery {
            postRepository.editPost(any(), any())
        } returns Unit
        // when
        runBlocking { postUseCaseImpl.editPost(sampleId, sampleContent) }
        // then
        coVerify(exactly = 1) { postRepository.editPost(sampleId, sampleContent) }
    }

     @Test
    // No return value
    // TODO: NOT FULLY IMPLEMENTED
    fun deletePost_verifyRunExactlyOnce() {
        // given
        val sampleId = "sampleId"
        coEvery {
            postRepository.deletePost(any())
        } returns Unit
        // when
        runBlocking { postUseCaseImpl.deletePost(sampleId) }
        // then
        coVerify(exactly = 1) { postRepository.deletePost(sampleId) }
    }
}