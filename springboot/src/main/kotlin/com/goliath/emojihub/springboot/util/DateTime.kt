package com.goliath.emojihub.springboot.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getDateTimeNow(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}