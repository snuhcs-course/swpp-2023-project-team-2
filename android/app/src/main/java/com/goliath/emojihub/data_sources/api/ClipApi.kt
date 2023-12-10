package com.goliath.emojihub.data_sources.api

import com.goliath.emojihub.models.ClipRequestDto
import com.goliath.emojihub.models.responses.ClipInferenceResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ClipApi {
    @POST("clip-vit-large-patch14")
    suspend fun runClipInference(
        @Body body: ClipRequestDto
    ): Response<List<ClipInferenceResponseDto>>
}
