package com.goliath.emojihub

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.PostDto
import com.goliath.emojihub.models.ReactionWithEmojiUnicode
import com.goliath.emojihub.models.UserDetailsDto
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
val sampleEmojiDto = EmojiDto(
    createdBy = "channn",
    createdAt = "2023-11-24 14:25:05",
    savedCount = 1600,
    videoLink = "https://storage.googleapis.com/emojihub-e2023.appspot.com/uu_2023-11-24%2014%3A25%3A05.mp4?GoogleAccessId=firebase-adminsdk-zynbm@emojihub-e2023.iam.gserviceaccount.com&Expires=1709443506&Signature=I%2BNRJSZ7nYtmrWs%2Fjv4uVAeW8%2BfHGF6GeV0pZRE4Sp5gCFuXLXBTKpgRBl1j2F%2BSSUStSqvBlktHZofZznGHWtsMYHQ99%2Bv7wcenqZweSWSmzse4s9sKAOkykn7pB9EMnFgax4VqGK4U5ey5HNSCKsjyNa5ZqDH8%2BqF%2FcIjQ3huChDMB2Xw1InaHUve0syvW6uz%2BeooDLo2nkGxdtElsDtomq2cAUMgk7nRNIYciYLGJ%2FsrscW7%2FXfD3rn%2BH3EM9z5S9DHKHWiEmh1xf0wpTtDsXom7p14XnZunnnOxpNO5OMFJi2x1kxZBFVc7U88V19eTmasWxdGV5TZipfN2ZMA%3D%3D",
    thumbnailLink = "https://storage.googleapis.com/emojihub-e2023.appspot.com/uu_2023-11-24%2014%3A25%3A05.jpeg?GoogleAccessId=firebase-adminsdk-zynbm@emojihub-e2023.iam.gserviceaccount.com&Expires=1709443506&Signature=lZK4otdQOXBVKz3EeOEgpSqAH5QE3U6KuTz8bo5RwYQ463i0cBEx44zVPJO3dIP%2B3%2FdKkBbJy%2BzIBogKAKUl5jLyP9FwInOZChspQOuI8zp%2FKivvEZImPnoG2C1UiiwB03tHYq0tWEhgj76BB4SarWRtZY4xRZhuVvuJg9%2FNV%2B5XZ7%2BGGjLbzfjc5rA45iwWQGPfgQN0%2FKJsdTieNb5%2F6%2B5QHW4pq7QLxYAGqvea5X6VY1JcUjXU0iZ%2FfI16L%2F1cFZAMPDPNPxC2bbllFH6vkOdb3qKuvGm0M3Y99GCLTv%2BAiObbBCs13AgmBO1OngrBV4db4zNnjUZOtB0rPRgyFw%3D%3D",
    id = "0ZF0MFHOV7974YTV3SBN",
    label = "love it",
    unicode = "U+2764 U+FE0F"
)

val dummyUsernames = listOf("channn", "doggydog", "meow_0w0", "mpunchmm", "kick_back")
val dummyUnicodes = listOf("U+1F44D", "U+1F600", "U+1F970", "U+1F60E", "U+1F621", "U+1F63A", "U+1F496", "U+1F415")
const val dummyMaxSavedCounts = 2000
fun createDeterministicTrendingEmojiDtoList(listSize : Int): Flow<PagingData<EmojiDto>> {
    val dummyEmojiList = mutableListOf<EmojiDto>()
    for (i in 0 until listSize) {
        dummyEmojiList.add(
            EmojiDto(
                createdBy = dummyUsernames[i % dummyUsernames.size],
                createdAt = "2023."+i%12+".16",
                savedCount = dummyMaxSavedCounts - i*10,
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
fun createDeterministicTrendingEmojiList(listSize: Int): Flow<PagingData<Emoji>> {
    return createDeterministicTrendingEmojiDtoList(listSize).map { it.map { dto -> Emoji(dto) } }
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
    reaction = listOf(
        ReactionWithEmojiUnicode("3456", "U+1F44D"),
        ReactionWithEmojiUnicode("5678", "U+1F44D")
    )
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
                reaction = listOf(
                    ReactionWithEmojiUnicode("3456", "U+1F44D"),
                    ReactionWithEmojiUnicode("5678", "U+1F44D")
                )
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
