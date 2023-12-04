package com.goliath.emojihub.springboot.global.exception

import org.springframework.http.HttpStatus
import com.goliath.emojihub.springboot.global.exception.ErrorType.*

open class CustomHttpException(errorTypeInterface: ErrorTypeInterface, val status: HttpStatus) : RuntimeException(errorTypeInterface.getMessage())

data class ErrorResponse(
    val errorCode: Int,
    val detail: String?
) {
    constructor(customHttpException: CustomHttpException): this(
        errorCode = customHttpException.status.value(),
        detail = customHttpException.message
    )
}

class CustomHttp400(badRequest: BadRequest) : CustomHttpException(badRequest, HttpStatus.BAD_REQUEST)

class CustomHttp401(unauthorized: Unauthorized) : CustomHttpException(unauthorized, HttpStatus.UNAUTHORIZED)

class CustomHttp403(forbidden: Forbidden) : CustomHttpException(forbidden, HttpStatus.FORBIDDEN)

class CustomHttp404(notFound: NotFound) : CustomHttpException(notFound, HttpStatus.NOT_FOUND)

class CustomHttp409(conflict: Conflict) : CustomHttpException(conflict, HttpStatus.CONFLICT)