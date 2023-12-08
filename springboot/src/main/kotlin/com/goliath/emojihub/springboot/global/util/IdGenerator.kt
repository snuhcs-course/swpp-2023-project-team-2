package com.goliath.emojihub.springboot.global.util

import kotlin.streams.asSequence

fun generateId(): String {
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val outputStrLength: Long = 20
    return java.util.Random().ints(outputStrLength, 0, source.length)
        .asSequence()
        .map(source::get)
        .joinToString("")
}