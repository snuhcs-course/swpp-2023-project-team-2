package com.goliath.emojihub.springboot.domain.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
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
){

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var postService: PostService

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

        val list = mutableListOf<PostDto>()
        val size = 2
        val id = "test_id"
        val createdBy = "test_created_by"
        val content = "test_content"
        val createdAt = "test_created_at"
        val modifiedAt = "test_modified_at"
        for (i in 0 until size) {
            list.add(
                PostDto(
                    id = id + i,
                    created_by = createdBy + i,
                    content = content + i,
                    created_at = createdAt + i,
                    modified_at = modifiedAt + i
                )
            )
        }
        given(
            postService.getPosts(
                anyInt(),
                anyInt(),
            )
        ).willReturn(list)

        // when
        val result = this.mockMvc.perform(
            get("/api/post")
                .param("index", index.toString())
                .param("count", count.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", Matchers.equalTo(size)))
            .andExpect(jsonPath("$[0].id").value(id + 0))
            .andExpect(jsonPath("$[0].created_by").value(createdBy + 0))
            .andExpect(jsonPath("$[0].content").value(content + 0))
            .andExpect(jsonPath("$[0].created_at").value(createdAt + 0))
            .andExpect(jsonPath("$[0].modified_at").value(modifiedAt + 0))
        verify(postService, times(1)).getPosts(index, count)
    }

    @Test
    @WithCustomUser
    @DisplayName("자신의 게시글 데이터 가져오기 테스트")
    fun getMyPosts() {
        // given
        val username = "custom_username"
        val list = mutableListOf<PostDto>()
        val size = 2
        val id = "test_id"
        val content = "test_content"
        val createdAt = "test_created_at"
        val modifiedAt = "test_modified_at"
        for (i in 0 until size) {
            list.add(
                PostDto(
                    id = id + i,
                    created_by = username,
                    content = content + i,
                    created_at = createdAt + i,
                    modified_at = modifiedAt + i
                )
            )
        }
        given(postService.getMyPosts(username)).willReturn(list)

        // when
        val result = this.mockMvc.perform(get("/api/post/me"))

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", Matchers.equalTo(size)))
            .andExpect(jsonPath("$[0].id").value(id + 0))
            .andExpect(jsonPath("$[0].created_by").value(username))
            .andExpect(jsonPath("$[0].content").value(content + 0))
            .andExpect(jsonPath("$[0].created_at").value(createdAt + 0))
            .andExpect(jsonPath("$[0].modified_at").value(modifiedAt + 0))
        verify(postService, times(1)).getMyPosts(username)
    }

    @Test
    @WithCustomUser
    @DisplayName("특정 게시글 데이터 가져오기 테스트")
    fun getPost() {
        // given
        val postDto = PostDto(
            id = "test_id",
            created_by = "test_created_by",
            content = "test_content",
            created_at = "test_created_at",
            modified_at = "test_modified_at",
        )
        given(postService.getPost(any())).willReturn(postDto)

        // when
        val result = mockMvc.perform(get("/api/post/{id}", postDto.id))

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(postDto.id))
            .andExpect(jsonPath("$.created_by").value(postDto.created_by))
            .andExpect(jsonPath("$.content").value(postDto.content))
            .andExpect(jsonPath("$.created_at").value(postDto.created_at))
            .andExpect(jsonPath("$.modified_at").value(postDto.modified_at))
        verify(postService, times(1)).getPost(postDto.id)
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