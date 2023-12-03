package com.goliath.emojihub.springboot.domain.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.WithCustomUser
import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.post.dto.PostRequest
import com.goliath.emojihub.springboot.domain.post.service.PostService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.DisplayName
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PostController::class)
internal class PostControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var postService: PostService

    companion object {
        private val testDto = TestDto()
        private val postList = testDto.postList
    }

    @Test
    @WithCustomUser
    @DisplayName("게시글 POST 테스트")
    fun postPost() {
        // given
        val username = "custom_username"
        val request = PostRequest(
            content = "test_content"
        )

        // when
        val result = mockMvc.perform(
            post("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )

        // then
        result.andExpect(status().isCreated)
        verify(postService, times(1)).postPost(username, request.content)
    }

    @Test
    @WithCustomUser
    @DisplayName("게시글 데이터 가져오기 테스트")
    fun getPosts() {
        // given
        val index = 1
        val count = 10
        given(
            postService.getPosts(
                anyInt(),
                anyInt(),
            )
        ).willReturn(postList)

        // when
        val result = this.mockMvc.perform(
            get("/api/post")
                .param("index", index.toString())
                .param("count", count.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", Matchers.equalTo(postList.size)))
            .andExpect(jsonPath("$[0].id").value(postList[0].id))
            .andExpect(jsonPath("$[0].created_by").value(postList[0].created_by))
            .andExpect(jsonPath("$[0].content").value(postList[0].content))
            .andExpect(jsonPath("$[0].created_at").value(postList[0].created_at))
            .andExpect(jsonPath("$[0].modified_at").value(postList[0].modified_at))
        verify(postService, times(1)).getPosts(index, count)
    }

    @Test
    @WithCustomUser
    @DisplayName("자신의 게시글 데이터 가져오기 테스트")
    fun getMyPosts() {
        // given
        val username = "custom_username"
        val realUsername = postList[0].created_by
        val index = 1
        val count = testDto.postSize
        val posts = mutableListOf<PostDto>()
        for (post in postList) {
            if (post.created_by == realUsername) {
                posts.add(post)
            }
        }
        given(postService.getMyPosts(username, index, count)).willReturn(posts)

        // when
        val result = this.mockMvc.perform(
            get("/api/post/me")
                .param("index", index.toString())
                .param("count", count.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", Matchers.equalTo(posts.size)))
            .andExpect(jsonPath("$[0].id").value(posts[0].id))
            .andExpect(jsonPath("$[0].created_by").value(posts[0].created_by))
            .andExpect(jsonPath("$[0].content").value(posts[0].content))
            .andExpect(jsonPath("$[0].created_at").value(posts[0].created_at))
            .andExpect(jsonPath("$[0].modified_at").value(posts[0].modified_at))
        verify(postService, times(1)).getMyPosts(username, index, count)
    }

    @Test
    @WithCustomUser
    @DisplayName("특정 게시글 데이터 가져오기 테스트")
    fun getPost() {
        // given
        val post = postList[0]
        given(postService.getPost(any())).willReturn(post)

        // when
        val result = mockMvc.perform(get("/api/post/{id}", post.id))

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(post.id))
            .andExpect(jsonPath("$.created_by").value(post.created_by))
            .andExpect(jsonPath("$.content").value(post.content))
            .andExpect(jsonPath("$.created_at").value(post.created_at))
            .andExpect(jsonPath("$.modified_at").value(post.modified_at))
        verify(postService).getPost(post.id)
    }

    @Test
    @WithCustomUser
    @DisplayName("게시글 수정 테스트")
    fun patchPost() {
        // given
        val username = "custom_username"
        val postId = "test_postId"
        val request = PostRequest(
            content = "test_content"
        )

        // when
        val result = mockMvc.perform(
            patch("/api/post/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(postService, times(1)).patchPost(username, postId, request.content)

    }

    @Test
    @WithCustomUser
    @DisplayName("게시글 삭제 테스트")
    fun deletePost() {
        // given
        val username = "custom_username"
        val postId = "test_postId"

        // when
        val result = mockMvc.perform(
            delete("/api/post/{id}", postId)
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(postService, times(1)).deletePost(username, postId)
    }
}