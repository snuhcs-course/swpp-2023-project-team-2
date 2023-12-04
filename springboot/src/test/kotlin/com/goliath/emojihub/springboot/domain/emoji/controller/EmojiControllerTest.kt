package com.goliath.emojihub.springboot.domain.emoji.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.goliath.emojihub.springboot.domain.TestDto
import com.goliath.emojihub.springboot.domain.WithCustomUser
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.domain.emoji.service.EmojiService
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.CREATED_EMOJIS
import com.goliath.emojihub.springboot.global.util.StringValue.UserField.SAVED_EMOJIS
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(EmojiController::class)
internal class EmojiControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var emojiService: EmojiService

    companion object {
        private val testDto = TestDto()
        val emojiList = testDto.emojiList
    }

    @Test
    @WithCustomUser
    @DisplayName("이모지 데이터 가져오기 테스트")
    fun getEmojis() {
        // given
        val sortByDate = 0
        val index = 1
        val count = 10
        Mockito.`when`(emojiService.getEmojis(sortByDate, index, count)).thenReturn(emojiList)

        // when
        val result = this.mockMvc.perform(
            get("/api/emoji")
                .param("sortByDate", sortByDate.toString())
                .param("index", index.toString())
                .param("count", count.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", equalTo(emojiList.size)))
            .andExpect(jsonPath("$[0].id").value(emojiList[0].id))
            .andExpect(jsonPath("$[0].created_by").value(emojiList[0].created_by))
            .andExpect(jsonPath("$[0].video_url").value(emojiList[0].video_url))
            .andExpect(jsonPath("$[0].emoji_unicode").value(emojiList[0].emoji_unicode))
            .andExpect(jsonPath("$[0].emoji_label").value(emojiList[0].emoji_label))
            .andExpect(jsonPath("$[0].created_at").value(emojiList[0].created_at))
            .andExpect(jsonPath("$[0].num_saved").value(emojiList[0].num_saved))
        verify(emojiService, times(1)).getEmojis(sortByDate, index, count)
    }

    @Test
    @WithCustomUser
    @DisplayName("자신이 만든 이모지 데이터 가져오기 테스트")
    fun getMyCreatedEmojis() {
        // given
        val username = "custom_username"
        val index = 1
        val count = testDto.createdEmojiSize
        Mockito.`when`(emojiService.getMyEmojis(username, CREATED_EMOJIS.string, index, count)).thenReturn(emojiList)

        // when
        val result = this.mockMvc.perform(
            get("/api/emoji/me/created")
                .param("index", index.toString())
                .param("count", count.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", equalTo(emojiList.size)))
            .andExpect(jsonPath("$[0].id").value(emojiList[0].id))
            .andExpect(jsonPath("$[0].created_by").value(emojiList[0].created_by))
            .andExpect(jsonPath("$[0].video_url").value(emojiList[0].video_url))
            .andExpect(jsonPath("$[0].emoji_unicode").value(emojiList[0].emoji_unicode))
            .andExpect(jsonPath("$[0].emoji_label").value(emojiList[0].emoji_label))
            .andExpect(jsonPath("$[0].created_at").value(emojiList[0].created_at))
            .andExpect(jsonPath("$[0].num_saved").value(emojiList[0].num_saved))
        verify(emojiService, times(1)).getMyEmojis(username, CREATED_EMOJIS.string, index, count)
    }

    @Test
    @WithCustomUser
    @DisplayName("자신이 저장한 이모지 데이터 가져오기 테스트")
    fun getMySavedEmojis() {
        // given
        val username = "custom_username"
        val index = 1
        val count = testDto.savedEmojiSize
        Mockito.`when`(emojiService.getMyEmojis(username, SAVED_EMOJIS.string, index, count)).thenReturn(emojiList)

        // when
        val result = this.mockMvc.perform(
            get("/api/emoji/me/saved")
                .param("index", index.toString())
                .param("count", count.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", equalTo(emojiList.size)))
            .andExpect(jsonPath("$[0].id").value(emojiList[0].id))
            .andExpect(jsonPath("$[0].created_by").value(emojiList[0].created_by))
            .andExpect(jsonPath("$[0].video_url").value(emojiList[0].video_url))
            .andExpect(jsonPath("$[0].emoji_unicode").value(emojiList[0].emoji_unicode))
            .andExpect(jsonPath("$[0].emoji_label").value(emojiList[0].emoji_label))
            .andExpect(jsonPath("$[0].created_at").value(emojiList[0].created_at))
            .andExpect(jsonPath("$[0].num_saved").value(emojiList[0].num_saved))
        verify(emojiService, times(1)).getMyEmojis(username, SAVED_EMOJIS.string, index, count)
    }

    @Test
    @WithCustomUser
    @DisplayName("특정 이모지 데이터 가져오기 테스트")
    fun getEmoji() {
        // given
        val emoji = emojiList[0]
        given(emojiService.getEmoji(any())).willReturn(emoji)

        // when
        val result = mockMvc.perform(get("/api/emoji/{id}", emoji.id))

        // then
        result.andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(emoji.id))
            .andExpect(jsonPath("$.created_by").value(emoji.created_by))
            .andExpect(jsonPath("$.video_url").value(emoji.video_url))
            .andExpect(jsonPath("$.emoji_unicode").value(emoji.emoji_unicode))
            .andExpect(jsonPath("$.emoji_label").value(emoji.emoji_label))
            .andExpect(jsonPath("$.created_at").value(emoji.created_at))
            .andExpect(jsonPath("$.num_saved").value(emoji.num_saved))
        verify(emojiService).getEmoji(emoji.id)
    }

    @Test
    @WithCustomUser
    @DisplayName("이모지 POST 테스트")
    fun postEmoji() {
        // given
        val audioContent = ByteArray(100)
        val audioFile = MockMultipartFile("file", "test.mp4", "audio/mp4", audioContent)
        val imageContent = ByteArray(100)
        val thumbnail = MockMultipartFile("thumbnail", "test.jpeg", "image/jpeg", imageContent)
        val request = PostEmojiRequest(
            emoji_unicode = "test_emoji_unicode",
            emoji_label = "test_emoji_label"
        )
        val requestJson = objectMapper.writeValueAsString(request)
        val requestFile = MockMultipartFile(
            "postEmojiRequest",
            "",
            "application/json",
            requestJson.toByteArray(StandardCharsets.UTF_8)
        )

        // when
        val result = mockMvc.perform(
            multipart("/api/emoji")
                .file(audioFile)
                .file(thumbnail)
                .file(requestFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .with(csrf())
        )

        // then
        result.andExpect(status().isCreated)
        verify(emojiService, times(1)).postEmoji(any(), any(), any(), any(), any())
    }

    @Test
    @WithCustomUser
    @DisplayName("이모지 save 테스트")
    fun saveEmoji() {
        // given
        val username = "custom_username"
        val emojiId = "test_emojiId"

        // when
        val result = mockMvc.perform(
            put("/api/emoji/save")
                .param("emojiId", emojiId)
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(emojiService, times(1)).saveEmoji(username, emojiId)
    }

    @Test
    @WithCustomUser
    @DisplayName("이모지 unsave 테스트")
    fun unSaveEmoji() {
        // given
        val username = "custom_username"
        val emojiId = "test_emojiId"

        // when
        val result = mockMvc.perform(
            put("/api/emoji/unsave")
                .param("emojiId", emojiId)
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(emojiService, times(1)).unSaveEmoji(username, emojiId)
    }

    @Test
    @WithCustomUser
    @DisplayName("이모지 삭제 테스트")
    fun deleteEmoji() {
        // given
        val username = "custom_username"
        val emojiId = "test_emojiId"

        // when
        val result = mockMvc.perform(
            delete("/api/emoji")
                .param("emojiId", emojiId)
                .with(csrf())
        )

        // then
        result.andExpect(status().isOk)
        verify(emojiService, times(1)).deleteEmoji(username, emojiId)
    }
}