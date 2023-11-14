package com.goliath.emojihub.usecases

import android.net.Uri
import android.util.Log
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.UploadEmojiDto
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    suspend fun fetchEmojiList(numInt: Int)
    fun createEmoji(videoUri: Uri): Pair<String, String>?

    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean
    suspend fun saveEmoji(id: String): Boolean
    suspend fun unSaveEmoji(id: String): Boolean
}

@Singleton
class EmojiUseCaseImpl @Inject constructor(
    private val repository: EmojiRepository,
    private val model: X3dRepository,
    private val errorController: ApiErrorController
): EmojiUseCase {
    override suspend fun fetchEmojiList(numInt: Int) {
        repository.fetchEmojiList(numInt)
    }

    override fun createEmoji(videoUri: Uri): Pair<String, String>? {
        return model.createEmoji(videoUri)
    }

    override suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        val dto = UploadEmojiDto(emojiUnicode, emojiLabel)
        return repository.uploadEmoji(videoFile, dto)
    }

    override suspend fun saveEmoji(id: String): Boolean {
        val response = repository.saveEmoji(id)
        response.let {
            if (it.isSuccessful) {
                Log.d("Emoji Saved", "Emoji Id: $id")
                return true
            } else {
                errorController.setErrorState(it.code())
                return false
            }
        }
    }

    override suspend fun unSaveEmoji(id: String): Boolean {
        val response = repository.unSaveEmoji(id)
        response.let {
            if (it.isSuccessful) {
                Log.d("Emoji Saved", "Emoji Id: $id")
                return true
            } else {
                errorController.setErrorState(it.code())
                return false
            }
        }
    }
}