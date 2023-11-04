package com.goliath.emojihub.usecases

import android.net.Uri
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    fun fetchEmojiList()
    fun createEmoji(videoUri: Uri): Pair<String, String>?
}

@Singleton
class EmojiUseCaseImpl @Inject constructor(
    private val repository: EmojiRepository,
    private val model: X3dRepository
): EmojiUseCase {
    override fun fetchEmojiList() {
        repository.fetchEmojiList()
    }

    override fun createEmoji(videoUri: Uri): Pair<String, String>? {
        return model.createEmoji(videoUri)
    }
}