package com.goliath.emojihub.viewmodels

import com.goliath.emojihub.createDeterministicDummyPostList
import com.goliath.emojihub.mockLogClass
import com.goliath.emojihub.usecases.PostUseCase
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
class PostViewModelTest {
    private val postUseCase = spyk<PostUseCase>()
    private val postViewModel = PostViewModel(postUseCase)

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
    fun fetchPostList_success_updatePostList() = runTest {
        // given
        val sampleFetchedPostList = createDeterministicDummyPostList(10)
        coEvery {
                postUseCase.fetchPostList()
        } returns sampleFetchedPostList
        // when
        postViewModel.fetchPostList()
        advanceUntilIdle()
        // then
        coVerify(exactly = 1) { postUseCase.fetchPostList() }
        coVerify(exactly = 1) { postUseCase.updatePostList(any()) }
    }

    @Test
    fun fetchMyPostList_success_updateMyPostList() = runTest {
        // given
        val sampleFetchedMyPostList = createDeterministicDummyPostList(10)
        coEvery {
            postUseCase.fetchMyPostList()
        } returns sampleFetchedMyPostList
        // when
        postViewModel.fetchMyPostList()
        advanceUntilIdle()
        // then
        coVerify(exactly = 1) { postUseCase.fetchMyPostList() }
        coVerify(exactly = 1) { postUseCase.updateMyPostList(any()) }
    }

    @Test
    fun uploadPost_withValidContent_returnsTrue() {
        // given
        val sampleContent = "sample content"
        coEvery {
            postUseCase.uploadPost(sampleContent)
        } returns true
        // when
        val isUploaded = runBlocking {
            postViewModel.uploadPost(sampleContent)
        }
        // then
        coVerify { postUseCase.uploadPost(sampleContent) }
        assertTrue(isUploaded)
    }

    @Test
    fun uploadPost_occursError_returnsFalse() {
        // given
        val sampleContent = "sample content"
        coEvery {
            postUseCase.uploadPost(sampleContent)
        } returns false
        // when
        val isUploaded = runBlocking {
            postViewModel.uploadPost(sampleContent)
        }
        // then
        coVerify { postUseCase.uploadPost(sampleContent) }
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