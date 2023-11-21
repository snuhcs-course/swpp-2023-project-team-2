package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.api.PostApi
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.UploadPostDto
import retrofit2.Response
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PostRepositoryImplTest {
    private val postApi = mockk<PostApi>()
    private val postRepositoryImpl = PostRepositoryImpl(postApi)

    private val samplePostDto = PostDto(
        id = "1234",
        createdAt = "2023.09.16",
        createdBy = "channn",
        content = "조금 전에 앞에 계신 분이 실수로 지갑을 흘리셨다. " +
                "지갑이 하수구 구멍으로 빠지려는 찰나, 발로 굴러가는 지갑을 막아서 다행히 참사는 막을 수 있었다. " +
                "지갑 주인분께서 감사하다고 카페 드림에서 커피도 한 잔 사주셨다.",
        modifiedAt = "2023.10.23"
        //reaction = listOf("good", "check", "good")
    )

    // @Test
    fun fetchPostList_returnsFlowOfPagingDataOfPostDto() {
        // given
        val samplePostResponseBody = List(5) { samplePostDto }
        every {
            runBlocking {
                postApi.fetchPostList(any())
            }
        } returns Response.success(samplePostResponseBody)
        // when
        val flowPagingPostData = runBlocking {
            postRepositoryImpl.fetchPostList()
        }
        // then
        verify { runBlocking { postApi.fetchPostList(any()) } }
        var postCount = 0
        TODO("NOT IMPLEMENTED YET")
    }

    @Test
    fun uploadPost_returnsSuccessResponse() {
        // given
        val uploadPostDto = UploadPostDto(samplePostDto.content)
        every {
            runBlocking {
                postApi.uploadPost(any())
            }
        } returns Response.success(mockk())
        // when
        val response = runBlocking {
            postRepositoryImpl.uploadPost(uploadPostDto)
        }
        // then
        verify { runBlocking { postApi.uploadPost(uploadPostDto) } }
        assertTrue(response.isSuccessful)
    }

    @Test
    fun uploadPost_returnsFailureResponse() {
        // given
        val uploadPostDto = UploadPostDto(samplePostDto.content)
        every {
            runBlocking {
                postApi.uploadPost(any())
            }
        } returns Response.error(400, mockk())
        // when
        val response = runBlocking {
            postRepositoryImpl.uploadPost(uploadPostDto)
        }
        // then
        verify { runBlocking { postApi.uploadPost(uploadPostDto) } }
        assertFalse(response.isSuccessful)
    }

    @Test
    fun getPostWithId_success_returnsPostDto() {
        // given
        val samplePostResponseBody = samplePostDto
        every {
            runBlocking {
                postApi.getPostWithId(any())
            }
        } returns Response.success(samplePostResponseBody)
        // when
        val postDto = runBlocking {
            postRepositoryImpl.getPostWithId("1234")
        }
        // then
        verify { runBlocking { postApi.getPostWithId("1234") } }
        assertEquals(samplePostDto, postDto)
    }

    @Test
    fun getPostWithId_failure_returnsNull() {
        // given
        every {
            runBlocking {
                postApi.getPostWithId(any())
            }
        } returns Response.error(400, mockk())
        // when
        val postDto = runBlocking {
            postRepositoryImpl.getPostWithId("1234")
        }
        // then
        verify { runBlocking { postApi.getPostWithId("1234") } }
        assertNull(postDto)
    }

    // @Test
    fun editPost_returnsSuccessResponse() {
        // given
        every {
            runBlocking {
                postApi.editPost(any(), any())
            }
        } returns Response.success(mockk())
        // when
        val response = runBlocking {
            postRepositoryImpl.editPost(samplePostDto.id, samplePostDto.content)
        }
        // then
        verify {
            runBlocking {
                postApi.editPost(
                    samplePostDto.id,
                    UploadPostDto(samplePostDto.content)
                )
            }
        }
        // TODO : Response is not implemented yet
        // assertTrue(response.isSuccessful)
    }

    // @Test
    fun deletePost_returnsSuccessResponse() {
        // given
        every {
            runBlocking {
                postApi.deletePost(any())
            }
        } returns Response.success(mockk())
        // when
        val response = runBlocking {
            postRepositoryImpl.deletePost(samplePostDto.id)
        }
        // then
        verify { runBlocking { postApi.deletePost(samplePostDto.id) } }
        // TODO : Response is not implemented yet
        // assertTrue(response.isSuccessful)
    }
}