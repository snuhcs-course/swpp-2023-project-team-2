package com.goliath.emojihub.springboot.controller

import com.goliath.emojihub.springboot.dto.emoji.EmojiDto
import com.goliath.emojihub.springboot.dto.emoji.PostEmojiRequest
import com.goliath.emojihub.springboot.service.EmojiService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/emoji")
class EmojiController (private val emojiService: EmojiService){

    // Get randomly selected emojis with a limit of `numLimit`
    @GetMapping
    fun getEmojis(numLimit: Int = 10): ResponseEntity<List<EmojiDto>> {
        return ResponseEntity.ok(emojiService.getEmojis(numLimit))
    }

    @GetMapping("/search")
    fun getEmoji(emojiId: String): ResponseEntity<EmojiDto> {
        return ResponseEntity.ok(emojiService.getEmoji(emojiId))
    }

    @PostMapping("/upload")
    fun postEmoji(
        @RequestBody postEmojiRequest: PostEmojiRequest
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.postEmoji(postEmojiRequest), HttpStatus.CREATED)
    }

    @PutMapping("/save")
    fun saveEmoji(
        @RequestBody userId: String, emojiId: String
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.saveEmoji(userId, emojiId), HttpStatus.CREATED)
    }

    @PutMapping("/unsave")
    fun unSaveEmoji(
        @RequestBody userId: String, emojiId: String
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.unSaveEmoji(userId, emojiId), HttpStatus.OK)
    }

    @DeleteMapping("/delete")
    fun deleteEmoji(
        @RequestBody emojiId: String
    ): ResponseEntity<Unit> {
        return ResponseEntity(emojiService.deleteEmoji(emojiId), HttpStatus.OK)
    }
}