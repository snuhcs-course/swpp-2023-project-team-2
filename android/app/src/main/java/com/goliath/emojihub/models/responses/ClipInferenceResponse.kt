package com.goliath.emojihub.models.responses

import com.google.gson.annotations.SerializedName

class ClipInferenceResponse(
    dto: ClipInferenceResponseDto
) {
    val emojiName: String = dto.emojiName
    val score: Double = dto.score
}

data class ClipInferenceResponseDto(
    @SerializedName("label") val emojiName: String,
    @SerializedName("score") val score: Double
)
