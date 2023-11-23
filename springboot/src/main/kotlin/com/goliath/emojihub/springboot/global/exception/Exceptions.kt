package com.goliath.emojihub.springboot.global.exception

import org.springframework.http.HttpStatus

open class CustomHttpException(msg: String, val status: HttpStatus) : RuntimeException(msg)

data class ErrorResponse(
    val errorCode: Int,
    val detail: String?
) {
    constructor(customHttpException: CustomHttpException): this(
        errorCode = customHttpException.status.value(),
        detail = customHttpException.message
    )
}

class CustomHttp400(msg: String) : CustomHttpException(msg, HttpStatus.BAD_REQUEST)

class CustomHttp401(msg: String) : CustomHttpException(msg, HttpStatus.UNAUTHORIZED)

class CustomHttp403(msg: String) : CustomHttpException(msg, HttpStatus.FORBIDDEN)

class CustomHttp404(msg: String) : CustomHttpException(msg, HttpStatus.NOT_FOUND)

class CustomHttp409(msg: String) : CustomHttpException(msg, HttpStatus.CONFLICT)

class CustomHttp502(msg: String) : CustomHttpException(msg, HttpStatus.BAD_GATEWAY)
