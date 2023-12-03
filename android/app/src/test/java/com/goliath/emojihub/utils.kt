package com.goliath.emojihub

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.X3dInferenceResult
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun mockLogClass() {
    mockkStatic(Log::class)
    every { Log.v(any(), any()) } returns 0
    every { Log.d(any(), any()) } returns 0
    every { Log.i(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0
}

// EMOJI TESTING UTILS
val dummyUsernames = listOf("channn", "doggydog", "meow_0w0", "mpunchmm", "kick_back")
val dummyUnicodes = listOf("U+1F44D", "U+1F600", "U+1F970", "U+1F60E", "U+1F621", "U+1F63A", "U+1F496", "U+1F415")
const val dummyMaxSavedCounts = 2000
fun createDeterministicDummyEmojiDtoList(listSize : Int): Flow<PagingData<EmojiDto>> {
    val dummyEmojiList = mutableListOf<EmojiDto>()
    for (i in 0 until listSize) {
        dummyEmojiList.add(
            EmojiDto(
                createdBy = dummyUsernames[i % dummyUsernames.size],
                createdAt = "2023.09.16",
                savedCount = dummyMaxSavedCounts % (i + 1),
                videoLink = "",
                thumbnailLink = "",
                unicode = dummyUnicodes[i % dummyUnicodes.size],
                id = "1234",
                label = "sample"
            )
        )
    }
    return flowOf(PagingData.from(dummyEmojiList))
}
fun createDeterministicDummyEmojiList(listSize: Int): Flow<PagingData<Emoji>> {
    return createDeterministicDummyEmojiDtoList(listSize).map { it.map { dto -> Emoji(dto) } }
}

// POST TESTING UTILS
val samplePostDto = PostDto(
    id = "1234",
    createdAt = "2023.09.16",
    createdBy = "channn",
    content = "조금 전에 앞에 계신 분이 실수로 지갑을 흘리셨다. " +
            "지갑이 하수구 구멍으로 빠지려는 찰나, 발로 굴러가는 지갑을 막아서 다행히 참사는 막을 수 있었다. " +
            "지갑 주인분께서 감사하다고 카페 드림에서 커피도 한 잔 사주셨다.",
    modifiedAt = "2023.10.23",
    reaction = listOf("good", "check", "good")
)
fun createDeterministicDummyPostDtoList(listSize : Int): Flow<PagingData<PostDto>> {
    val dummyPostList = mutableListOf<PostDto>()
    for (i in 0 until listSize) {
        dummyPostList.add(
            PostDto(
                id = "1234",
                createdAt = "2023.09.16",
                createdBy = "channn",
                content = "조금 전에 앞에 계신 분이 실수로 지갑을 흘리셨다. " +
                        "지갑이 하수구 구멍으로 빠지려는 찰나, 발로 굴러가는 지갑을 막아서 다행히 참사는 막을 수 있었다. " +
                        "지갑 주인분께서 감사하다고 카페 드림에서 커피도 한 잔 사주셨다.",
                modifiedAt = "2023.10.23",
                reaction = listOf("good", "check", "good")
            )
        )
    }
    return flowOf(PagingData.from(dummyPostList))
}
fun createDeterministicDummyPostList(listSize: Int): Flow<PagingData<Post>> {
    return createDeterministicDummyPostDtoList(listSize).map { it.map { dto -> Post(dto) } }
}

// X3D TESTING UTILS

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
