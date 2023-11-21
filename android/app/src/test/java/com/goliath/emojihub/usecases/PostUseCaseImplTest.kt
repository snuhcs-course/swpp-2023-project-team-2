package com.goliath.emojihub.usecases

import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import com.goliath.emojihub.repositories.remote.PostRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
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
    private val apiErrorController = mockk<ApiErrorController>()
    private val postUseCaseImpl = PostUseCaseImpl(postRepository, apiErrorController)

    @Test
    fun updatePostList_withSamplePagingPostData_updatesPostListStateFlow() {
        // given
        val samplePagingPostData = mockk<PagingData<Post>>()
        // when
        runBlocking {
            postUseCaseImpl.updatePostList(samplePagingPostData)
        }
        // then
        assertEquals(samplePagingPostData, postUseCaseImpl.postList.value)
    }

//    @Test
    fun fetchPostList_returnsFlowOfPostPagingData() {
        // given
        val samplePostPagingDataFlow = mockk<Flow<PagingData<PostDto>>>()
        every {
            runBlocking {
                postRepository.fetchPostList()
            }
        } returns samplePostPagingDataFlow
        // when
        val fetchedPostPagingDataFlow = runBlocking {
            postUseCaseImpl.fetchPostList()
        }
        // then
        verify { runBlocking { postRepository.fetchPostList() } }
        TODO("fix this test")
        val sampleFetchedPostPagingDataFlow =
            samplePostPagingDataFlow.map { it.map { dto -> Post(dto) } }
        assertEquals(sampleFetchedPostPagingDataFlow, fetchedPostPagingDataFlow)
    }

    @Test
    fun uploadPost_success_returnsTrue() {
        // given
        val sampleContent = "sample content"
        every {
            runBlocking {
                postRepository.uploadPost(any())
            }
        } returns Response.success(mockk())
        // when
        val isSuccess = runBlocking {
            postUseCaseImpl.uploadPost(sampleContent)
        }
        // then
        verify { runBlocking { postRepository.uploadPost(UploadPostDto(sampleContent)) } }
        assertTrue(isSuccess)
    }

    // @Test
    fun getPostWithId() {
    }

    // @Test
    fun editPost() {
    }

    // @Test
    fun deletePost() {
    }
}