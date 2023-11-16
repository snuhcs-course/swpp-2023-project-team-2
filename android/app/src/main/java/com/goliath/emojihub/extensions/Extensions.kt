package com.goliath.emojihub.extensions

fun String.toEmoji(): String {
    return String(Character.toChars(this.substring(2).toInt(16)))
}