package com.goliath.emojihub.repositories.remote

import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    fun fetchEmojiList()
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(

): EmojiRepository {
    override fun fetchEmojiList() {
        TODO("Not yet implemented")
    }
}