package com.goliath.emojihub.springboot.domain.emoji.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.goliath.emojihub.springboot.domain.WithCustomUser
import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.domain.emoji.service.EmojiService
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


    @Test
    @WithCustomUser
    @DisplayName("이모지 데이터 가져오기 테스트")
    fun getEmojis() {
        // given
        val sortByDate = 0
        val index = 1
        val count = 10
        val list = mutableListOf<EmojiDto>()
        val size = 2
        val id = "test_id"
        val createdBy = "test_created_by"
        val videoUrl = "test_video_url"
        val emojiUnicode = "test_emoji_unicode"
        val emojiLabel = "test_emoji_label"
        val createdAt = "test_created_at"
        for (i in 0 until size) {
            list.add(
                EmojiDto(
                    id = id + i,
                    created_by = createdBy + i,
                    video_url = videoUrl + i,
                    emoji_unicode = emojiUnicode + i,
                    emoji_label = emojiLabel + i,
                    created_at = createdAt + i,
                    num_saved = i.toLong()
                )
            )
        }
        given(
            emojiService.getEmojis(
                anyInt(),
                anyInt(),
                anyInt()
            )
        ).willReturn(list)

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
            .andExpect(jsonPath("$.length()", Matchers.equalTo(size)))
            .andExpect(jsonPath("$[0].id").value(id + 0))
            .andExpect(jsonPath("$[0].created_by").value(createdBy + 0))
            .andExpect(jsonPath("$[0].video_url").value(videoUrl + 0))
            .andExpect(jsonPath("$[0].emoji_unicode").value(emojiUnicode + 0))
            .andExpect(jsonPath("$[0].emoji_label").value(emojiLabel + 0))
            .andExpect(jsonPath("$[0].created_at").value(createdAt + 0))
            .andExpect(jsonPath("$[0].num_saved").value(0))
        verify(emojiService).getEmojis(sortByDate, index, count)

    }

    @Test
    @WithCustomUser
    @DisplayName("특정 이모지 데이터 가져오기 테스트")
    fun getEmoji() {
        // given
        val emojiDto = EmojiDto(
            id = "test_id",
            created_by = "test_created_by",
            video_url = "test_video_url",
            emoji_unicode = "test_emoji_unicode",
            emoji_label = "test_emoji_label",
            created_at = "test_created_at",
            num_saved = 1
        )
        given(emojiService.getEmoji(any())).willReturn(emojiDto)

        // when
        val result = mockMvc.perform(get("/api/emoji/{id}", emojiDto.id))

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(emojiDto.id))
            .andExpect(jsonPath("$.created_by").value(emojiDto.created_by))
            .andExpect(jsonPath("$.video_url").value(emojiDto.video_url))
            .andExpect(jsonPath("$.emoji_unicode").value(emojiDto.emoji_unicode))
            .andExpect(jsonPath("$.emoji_label").value(emojiDto.emoji_label))
            .andExpect(jsonPath("$.created_at").value(emojiDto.created_at))
            .andExpect(jsonPath("$.num_saved").value(emojiDto.num_saved))
        verify(emojiService).getEmoji(emojiDto.id)
    }

    @Test
    @WithCustomUser
    @DisplayName("이모지 POST 테스트")
    fun postEmoji() {
        // given
        val audioContent = ByteArray(100)
        val audioFile = MockMultipartFile("file", "test.mp4", "audio/mp4", audioContent)
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
                .file(requestFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .with { request -> request.method = "POST"; request }
                .with(csrf())
        )

        // then
        result.andExpect(status().isCreated)
        verify(emojiService, times(1)).postEmoji(any(), any(), any())
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