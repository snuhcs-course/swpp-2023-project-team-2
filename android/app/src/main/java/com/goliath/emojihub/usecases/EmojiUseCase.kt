package com.goliath.emojihub.usecases

import com.goliath.emojihub.repositories.remote.EmojiRepository
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    fun fetchEmojiList()
}

@Singleton
class EmojiUseCaseImpl @Inject constructor(
    private val repository: EmojiRepository
): EmojiUseCase {
    override fun fetchEmojiList() {
        repository.fetchEmojiList()
    }
}