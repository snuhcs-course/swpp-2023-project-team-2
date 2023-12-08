package com.goliath.emojihub.models

import com.google.gson.annotations.SerializedName


class ClipRequestDto(
    @SerializedName("inputs") val image: String,
    // it contains the candidate emoji names
    @SerializedName("parameters") val parameters: Map<String, List<String>>
)