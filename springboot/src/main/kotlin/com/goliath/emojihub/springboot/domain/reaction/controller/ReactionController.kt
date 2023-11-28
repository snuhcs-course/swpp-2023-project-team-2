package com.goliath.emojihub.springboot.domain.reaction.controller

import com.goliath.emojihub.springboot.domain.reaction.dto.ReactionDto
import com.goliath.emojihub.springboot.domain.reaction.service.ReactionService
import com.goliath.emojihub.springboot.domain.user.model.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reaction")
class ReactionController(private val reactionService: ReactionService) {

    @GetMapping
    fun getReactionsOfPost(
        @RequestParam(value = "postId", defaultValue = "") postId: String,
    ): ResponseEntity<List<ReactionDto>> {
        return ResponseEntity.ok(reactionService.getReactionsOfPost(postId))
    }

    @PostMapping
    fun postReaction(
        @CurrentUser username: String,
        @RequestParam(value = "postId", defaultValue = "") postId: String,
        @RequestParam(value = "emojiId", defaultValue = "") emojiId: String

    ): ResponseEntity<Unit> {
        return ResponseEntity(
            reactionService.postReaction(username, postId, emojiId), HttpStatus.CREATED
        )
    }

    @DeleteMapping
    fun deleteReaction(
        @CurrentUser username: String,
        @RequestParam(value = "reactionId", defaultValue = "") reactionId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(reactionService.deleteReaction(username, reactionId))
    }
}