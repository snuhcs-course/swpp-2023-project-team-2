package com.goliath.emojihub.models

data class CreatedEmoji (
    val emojiClassName: String,
    val emojiUnicode: String
)

data class X3dInferenceResult (
    val scoreIdx: Int,
    val score: Float
)