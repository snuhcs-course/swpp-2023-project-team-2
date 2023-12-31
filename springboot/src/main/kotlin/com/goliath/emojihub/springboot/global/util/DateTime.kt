package com.goliath.emojihub.springboot.global.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getDateTimeNow(): String {
    return LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}