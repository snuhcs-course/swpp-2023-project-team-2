package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.api.EmojiApi
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    fun fetchEmojiList()
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(
    private val emojiApi: EmojiApi
): EmojiRepository {
    override fun fetchEmojiList() {
        TODO("Not yet implemented")
    }
}