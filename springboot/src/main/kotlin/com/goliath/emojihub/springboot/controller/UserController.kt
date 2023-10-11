package com.goliath.emojihub.springboot.controller

import com.goliath.emojihub.springboot.model.User
import com.goliath.emojihub.springboot.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class UserController (private val userService: UserService) {
    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.getUsers())
    }
}