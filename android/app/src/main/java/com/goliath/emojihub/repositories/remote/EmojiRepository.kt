package com.goliath.emojihub.repositories.remote

import com.goliath.emojihub.data_sources.UserApi
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    fun fetchEmojiList()
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(
    // added temporarily for building project. will be soon replaced by `EmojiApi`
    private val userApi: UserApi
): EmojiRepository {
    override fun fetchEmojiList() {
        TODO("Not yet implemented")
    }
}