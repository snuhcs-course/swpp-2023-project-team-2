package com.goliath.emojihub.springboot.domain.post.controller

import com.goliath.emojihub.springboot.domain.post.dto.PostDto
import com.goliath.emojihub.springboot.domain.post.dto.PostRequest
import com.goliath.emojihub.springboot.domain.post.service.PostService
import com.goliath.emojihub.springboot.domain.user.model.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/post")
class PostController(private val postService: PostService) {

    @PostMapping
    fun postPost(
        @CurrentUser username: String,
        @RequestBody postRequest: PostRequest,
    ): ResponseEntity<Unit> {
        return ResponseEntity(
            postService.postPost(username, postRequest.content), HttpStatus.CREATED
        )
    }

    @GetMapping
    fun getPosts(
        @RequestParam(value = "index", defaultValue = 1.toString()) index: Int,
        @RequestParam(value = "count", defaultValue = 10.toString()) count: Int
    ): ResponseEntity<List<PostDto>> {
        return ResponseEntity.ok(postService.getPosts(index, count))
    }

    @GetMapping("/me")
    fun getMyPosts(
        @CurrentUser username: String
    ): ResponseEntity<List<PostDto>>{
        return ResponseEntity.ok(postService.getMyPosts(username))
    }

    @GetMapping("/{id}")
    fun getPost(
        @PathVariable(value = "id") id: String
    ): ResponseEntity<PostDto> {
        return ResponseEntity.ok(postService.getPost(id))
    }

    @PatchMapping("/{id}")
    fun patchPost(
        @CurrentUser username: String,
        @PathVariable(value = "id") id: String,
        @RequestBody postRequest: PostRequest,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(
            postService.patchPost(username, id, postRequest.content)
        )
    }

    @DeleteMapping("/{id}")
    fun deletePost(
        @CurrentUser username: String,
        @PathVariable(value = "id") id: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(postService.deletePost(username, id))
    }
}