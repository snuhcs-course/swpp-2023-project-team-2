package com.goliath.emojihub.springboot.controller

import com.goliath.emojihub.springboot.dto.SignUpRequest
import com.goliath.emojihub.springboot.dto.UserDto
import com.goliath.emojihub.springboot.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController (private val userService: UserService) {
    @GetMapping
    fun getUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(userService.getUsers())
    }

    @PostMapping("/signup")
    fun signUp(
        @RequestBody signUpRequest: SignUpRequest
    ): ResponseEntity<Unit> {
        return ResponseEntity(userService.signUp(signUpRequest), HttpStatus.CREATED)
    }
}