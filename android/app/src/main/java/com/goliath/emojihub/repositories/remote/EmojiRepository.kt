package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.repositories.data_sources.EmojiDataSource
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    fun fetchEmojiList()
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(
    private val dataSource: EmojiDataSource
): EmojiRepository {
    override fun fetchEmojiList() {
        TODO("Not yet implemented")
    }
}