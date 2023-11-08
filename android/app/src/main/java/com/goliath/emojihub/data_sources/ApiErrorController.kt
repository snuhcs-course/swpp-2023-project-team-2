package com.goliath.emojihub.data_sources

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ApiErrorController {
    val apiErrorState: StateFlow<CustomError?>
    fun setErrorState(errorCode: Int)
    fun dismiss()
}

@Singleton
class ApiErrorControllerImpl @Inject constructor(

): ApiErrorController {

    private val _apiErrorState = MutableStateFlow<CustomError?>(null)
    override val apiErrorState: StateFlow<CustomError?>
        get() = _apiErrorState

    override fun setErrorState(errorCode: Int) {
        _apiErrorState.update {
            when (errorCode) {
                CustomError.BAD_REQUEST.ordinal -> CustomError.BAD_REQUEST
                CustomError.UNAUTHORIZED.ordinal -> CustomError.UNAUTHORIZED
                CustomError.FORBIDDEN.ordinal -> CustomError.FORBIDDEN
                CustomError.NOT_FOUND.ordinal -> CustomError.NOT_FOUND
                CustomError.CONFLICT.ordinal -> CustomError.CONFLICT
                else -> CustomError.BAD_REQUEST
            }
        }
    }

    override fun dismiss() {
        _apiErrorState.update { null }
    }
}

enum class CustomError(
    statusCode: Int
) {
    BAD_REQUEST(400) {
        override fun body(): String = "잘못된 요청입니다."
    },
    UNAUTHORIZED(401) {
        override fun body(): String = "인증되지 않은 유저입니다."
    },
    FORBIDDEN(403) {
        override fun body(): String = "잘못된 접근입니다."
    },
    NOT_FOUND(404) {
        override fun body(): String = "요청하신 정보를 찾을 수 없습니다."
    },
    CONFLICT(409) {
        override fun body(): String = "이미 있는 계정입니다."
    };

    abstract fun body(): String
}