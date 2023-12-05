package com.goliath.emojihub.springboot.domain.reaction.controller

import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.WithCustomUser
import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionWithEmoji
import com.goliath.emojihub.springboot.domain.reaction.service.ReactionService
import org.hamcrest.Matchers.equalTo
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
        val post = testDto.postList[0]
        val postId = post.id
        val emojiUnicode = ""
        val index = 1
        val count = 10
        val reactionWithEmojiList = mutableListOf<ReactionWithEmoji>()
        for (reaction in reactionList) {
            if (reaction.post_id != postId) continue
            for (emoji in testDto.emojiList) {
                if (emoji.id != reaction.emoji_id) continue
                reactionWithEmojiList.add(
                    ReactionWithEmoji(reaction, emoji)
                )
            }
        }

        Mockito.`when`(reactionService.getReactionsOfPost(postId, emojiUnicode, index, count)).thenReturn(reactionWithEmojiList)

        // when
        val result = this.mockMvc.perform(
            get("/api/reaction")
                .param("postId", postId)
                .param("emojiUnicode", emojiUnicode)
                .param("index", index.toString())
                .param("count", count.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", equalTo(reactionWithEmojiList.size)))
            .andExpect(jsonPath("$[0]").value(reactionWithEmojiList[0]))
        verify(reactionService, times(1)).getReactionsOfPost(postId, emojiUnicode, index, count)
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