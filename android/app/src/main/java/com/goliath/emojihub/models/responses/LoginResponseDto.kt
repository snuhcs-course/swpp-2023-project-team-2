package com.goliath.emojihub.models.responses

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("accessToken") val accessToken: String
)