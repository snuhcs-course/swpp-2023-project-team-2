package com.goliath.emojihub.springboot.domain.emoji.controller

import com.goliath.emojihub.springboot.domain.emoji.dto.EmojiDto
import com.goliath.emojihub.springboot.domain.emoji.dto.GetEmojisRequest
import com.goliath.emojihub.springboot.domain.emoji.dto.PostEmojiRequest
import com.goliath.emojihub.springboot.domain.emoji.service.EmojiService
import com.goliath.emojihub.springboot.domain.user.model.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/emoji")
class EmojiController (private val emojiService: EmojiService){

    // Get randomly selected emojis with a limit of `numLimit`
    @GetMapping
    fun getEmojis(
        @RequestBody getEmojisRequest: GetEmojisRequest
    ): ResponseEntity<List<EmojiDto>> {
        return ResponseEntity.ok(emojiService.getEmojis(getEmojisRequest.sortByDate, getEmojisRequest.index, getEmojisRequest.count))
    }

    @GetMapping("/{id}")
    fun getEmoji(
        @PathVariable(value = "id") id: String,
    ): ResponseEntity<EmojiDto> {
        return ResponseEntity.ok(emojiService.getEmoji(id))
    }

    @PostMapping
    fun postEmoji(
        @CurrentUser username: String,
        @RequestPart(value = "file") file: MultipartFile,
        @RequestPart postEmojiRequest: PostEmojiRequest
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.postEmoji(username, file, postEmojiRequest), HttpStatus.CREATED)
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