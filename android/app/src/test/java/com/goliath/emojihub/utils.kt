package com.goliath.emojihub

import android.util.Log
import com.goliath.emojihub.models.X3dInferenceResult
import io.mockk.every
import io.mockk.mockkStatic

fun mockLogClass() {
    mockkStatic(Log::class)
    every { Log.v(any(), any()) } returns 0
    every { Log.d(any(), any()) } returns 0
    every { Log.i(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0
}


val sampleX3dInferenceResultListOverScoreThreshold = listOf(
    X3dInferenceResult(4, 0.8f),
    X3dInferenceResult(6, 0.15f),
    X3dInferenceResult(0, 0.01f)
)

val sampleX3dInferenceResultListUnderScoreThreshold = listOf(
    X3dInferenceResult(4, 0.3f),
    X3dInferenceResult(6, 0.15f),
    X3dInferenceResult(0, 0.01f)
)