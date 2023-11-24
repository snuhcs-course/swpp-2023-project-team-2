package com.goliath.emojihub.springboot.domain.reaction.controller

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.WithCustomUser
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.reaction.service.ReactionService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito
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

@WebMvcTest(ReactionController::class)
internal class ReactionControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @MockBean
    lateinit var reactionService: ReactionService

    companion object {
        private val testDto = TestDto()
        val reactionList = testDto.reactionList
    }

    @Test
    @WithCustomUser
    @DisplayName("게시글의 리액션 가져오기 테스트")
    fun getReactionsOfPost() {
        // given
        val postId = reactionList[0].post_id
        val reactions = mutableListOf<ReactionDto>()
        for (reaction in reactionList) {
            if (reaction.post_id == postId) {
                reactions.add(reaction)
            }
        }
        Mockito.`when`(reactionService.getReactionsOfPost(postId)).thenReturn(reactions)

        // when
        val result = this.mockMvc.perform(
            get("/api/reaction")
                .param("postId", postId)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", Matchers.equalTo(reactions.size)))
            .andExpect(jsonPath("$[0].id").value(reactions[0].id))
            .andExpect(jsonPath("$[0].created_by").value(reactions[0].created_by))
            .andExpect(jsonPath("$[0].post_id").value(reactions[0].post_id))
            .andExpect(jsonPath("$[0].emoji_id").value(reactions[0].emoji_id))
            .andExpect(jsonPath("$[0].created_at").value(reactions[0].created_at))
        verify(reactionService, times(1)).getReactionsOfPost(postId)
    }

    @Test
    @WithCustomUser
    @DisplayName("리액션 올리기 테스트")
    fun postReaction() {
        // given
        val username = "custom_username"
        val postId = testDto.postList[0].id
        val emojiId = testDto.emojiList[0].id

        // when
        val result = this.mockMvc.perform(
            post("/api/reaction")
                .param("postId", postId)
                .param("emojiId", emojiId)
                .with(csrf())
        )

        // then
        result.andExpect(status().isCreated)
        verify(reactionService, times(1)).postReaction(username, postId, emojiId)
    }

    @Test
    @WithCustomUser
    @DisplayName("리액션 삭제하기 테스트")
    fun deleteReaction() {
        // given
        val username = "custom_username"
        val reactionId = testDto.reactionList[0].id

        // when
        val result = this.mockMvc.perform(
            delete("/api/reaction")
                .param("reactionId", reactionId)
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(reactionService, times(1)).deleteReaction(username, reactionId)
    }
}