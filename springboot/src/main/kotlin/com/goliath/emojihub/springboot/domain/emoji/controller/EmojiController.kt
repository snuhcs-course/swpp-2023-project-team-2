package com.goliath.emojihub.springboot.domain.emoji.controller

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.domain.emoji.service.EmojiService
import com.goliath.emojihub.springboot.domain.user.model.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/emoji")
class EmojiController(private val emojiService: EmojiService) {

    companion object {
        const val CREATED_EMOJIS = "created_emojis"
        const val SAVED_EMOJIS = "saved_emojis"
    }

    @PostMapping
    fun postEmoji(
        @CurrentUser username: String,
        @RequestPart(value = "file") file: MultipartFile,
        @RequestPart(value = "thumbnail") thumbnail: MultipartFile,
        @RequestPart postEmojiRequest: PostEmojiRequest
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.postEmoji(username, file, thumbnail, postEmojiRequest.emoji_unicode, postEmojiRequest.emoji_label), HttpStatus.CREATED)
    }

    @GetMapping
    fun getEmojis(
        @RequestParam(value = "sortByDate", defaultValue = 0.toString()) sortByDate: Int,
        @RequestParam(value = "index", defaultValue = 1.toString()) index: Int,
        @RequestParam(value = "count", defaultValue = 10.toString()) count: Int,
    ): ResponseEntity<List<EmojiDto>> {
        return ResponseEntity.ok(emojiService.getEmojis(sortByDate, index, count))
    }

    @GetMapping("/me/created")
    fun getMyCreatedEmojis(
        @CurrentUser username: String,
        @RequestParam(value = "index", defaultValue = 1.toString()) index: Int,
        @RequestParam(value = "count", defaultValue = 10.toString()) count: Int,
    ): ResponseEntity<List<EmojiDto>> {
        return ResponseEntity.ok(emojiService.getMyEmojis(username, CREATED_EMOJIS, index, count))
    }

    @GetMapping("/me/saved")
    fun getMySavedEmojis(
        @CurrentUser username: String,
        @RequestParam(value = "index", defaultValue = 1.toString()) index: Int,
        @RequestParam(value = "count", defaultValue = 10.toString()) count: Int,
    ): ResponseEntity<List<EmojiDto>> {
        return ResponseEntity.ok(emojiService.getMyEmojis(username, SAVED_EMOJIS, index, count))
    }

    @GetMapping("/{id}")
    fun getEmoji(
        @PathVariable(value = "id") id: String,
    ): ResponseEntity<EmojiDto> {
        return ResponseEntity.ok(emojiService.getEmoji(id))
    }

    @PutMapping("/save")
    fun saveEmoji(
        @CurrentUser username: String,
        @RequestParam(value = "emojiId", defaultValue = "") emojiId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.saveEmoji(username, emojiId), HttpStatus.OK)
    }

    @PutMapping("/unsave")
    fun unSaveEmoji(
        @CurrentUser username: String,
        @RequestParam(value = "emojiId", defaultValue = "") emojiId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.unSaveEmoji(username, emojiId), HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteEmoji(
        @CurrentUser username: String,
        @RequestParam(value = "emojiId", defaultValue = "") emojiId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.deleteEmoji(username, emojiId), HttpStatus.OK)
    }
}