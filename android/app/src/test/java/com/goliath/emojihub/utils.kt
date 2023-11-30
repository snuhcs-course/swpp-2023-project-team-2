package com.goliath.emojihub

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.EmojiDto
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