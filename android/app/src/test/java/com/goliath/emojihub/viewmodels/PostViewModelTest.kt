package com.goliath.emojihub.viewmodels

import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.repositories.remote.PostRepository
import com.goliath.emojihub.usecases.PostUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {
    private val postRepository = mockk<PostRepository>()
    private val apiErrorController = mockk<ApiErrorController>()
    private val postUseCase = spyk(PostUseCaseImpl
        (postRepository, apiErrorController)
    )
    private val postViewModel = PostViewModel(postUseCase)

    private val testDispatcher = StandardTestDispatcher()
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }
    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchPostList_success_updatePostList() {
        // given
        val samplePagingDataFlow = spyk<Flow<PagingData<Post>>>()
        coEvery {
                postUseCase.fetchPostList()
        } returns samplePagingDataFlow
        // when
        runBlocking { postViewModel.fetchPostList() }
        // then
        coVerify { postUseCase.fetchPostList() }
        verify { runBlocking { postUseCase.updatePostList(any()) } }
        assertEquals(samplePagingDataFlow, postViewModel.postList)
    }

    @Test
    fun uploadPost_withValidContent_returnsTrue() {
        // given
        val sampleContent = "sample content"
        every {
            runBlocking {
                postUseCase.uploadPost(sampleContent)
            }
        } returns true
        // when
        val isUploaded = runBlocking {
            postViewModel.uploadPost(sampleContent)
        }
        // then
        verify { runBlocking { postUseCase.uploadPost(sampleContent) } }
        assertTrue(isUploaded)
    }

    @Test
    fun uploadPost_occursError_returnsFalse() {
        // given
        val sampleContent = "sample content"
        every {
            runBlocking {
                postUseCase.uploadPost(sampleContent)
            }
        } returns false
        // when
        val isUploaded = runBlocking {
            postViewModel.uploadPost(sampleContent)
        }
        // then
        verify { runBlocking { postUseCase.uploadPost(sampleContent) } }
        assertFalse(isUploaded)
    }

    // @Test
    // No return value
    fun getPostWithId() {
    }

    // @Test
    // No return value
    fun editPost() {
    }

    // @Test
    // No return value
    fun deletePost() {
    }
}