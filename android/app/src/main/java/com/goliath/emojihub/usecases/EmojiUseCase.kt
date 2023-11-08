package com.goliath.emojihub.usecases

import android.net.Uri
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    suspend fun fetchEmojiList(numInt: Int)
    fun createEmoji(videoUri: Uri): Pair<String, String>?

    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean
}

@Singleton
class EmojiUseCaseImpl @Inject constructor(
    private val repository: EmojiRepository,
    private val model: X3dRepository
): EmojiUseCase {
    override suspend fun fetchEmojiList(numInt: Int) {
        repository.fetchEmojiList(numInt)
    }

    override fun createEmoji(videoUri: Uri): Pair<String, String>? {
        return model.createEmoji(videoUri)
    }

    override suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        return repository.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
    }
}