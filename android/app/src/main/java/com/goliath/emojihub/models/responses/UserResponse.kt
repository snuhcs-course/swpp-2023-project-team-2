package com.goliath.emojihub.models.responses

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("access_token") val token: String
)