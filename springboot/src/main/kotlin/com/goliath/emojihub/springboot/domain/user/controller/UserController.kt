package com.goliath.emojihub.springboot.domain.user.controller

import com.goliath.emojihub.springboot.domain.user.dto.LoginRequest
import com.goliath.emojihub.springboot.domain.user.dto.SignUpRequest
import com.goliath.emojihub.springboot.domain.user.dto.UserDto
import com.goliath.emojihub.springboot.domain.user.model.CurrentUser
import com.goliath.emojihub.springboot.domain.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(userService.getUsers())
    }

    @PostMapping("/signup")
    fun signUp(
        @RequestBody signUpRequest: SignUpRequest
    ): ResponseEntity<UserDto.AuthToken> {
        return ResponseEntity(
            userService.signUp(signUpRequest.email, signUpRequest.username, signUpRequest.password),
            HttpStatus.CREATED
        )
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<UserDto.AuthToken> {
        return ResponseEntity.ok(userService.login(loginRequest.username, loginRequest.password))
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Unit> {
        return ResponseEntity.ok(userService.logout())
    }

    @DeleteMapping("/signout")
    fun signOut(
        @CurrentUser username: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(userService.signOut(username))
    }
}