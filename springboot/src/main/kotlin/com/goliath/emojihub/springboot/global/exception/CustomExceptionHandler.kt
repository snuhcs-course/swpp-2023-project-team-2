package com.goliath.emojihub.springboot.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    fun handle(e: Exception): ResponseEntity<Any> {
        return ResponseEntity(e.javaClass, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(value = [CustomHttpException::class])
    fun handle(e: CustomHttpException): ResponseEntity<Any> {
        return ResponseEntity(ErrorResponse(e), e.status)
    }
}