package com.goliath.emojihub.usecases

import android.net.Uri
import android.util.Log
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.EmojiDto

import com.goliath.emojihub.models.UploadEmojiDto
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    val emojiListState: StateFlow<List<EmojiDto>>
    suspend fun fetchEmojiList(numInt: Int)
    suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji>?
    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean
    suspend fun saveEmoji(id: String): Boolean
    suspend fun unSaveEmoji(id: String): Boolean
}

@Singleton
class EmojiUseCaseImpl @Inject constructor(
    private val emojiRepository: EmojiRepository,
    private val x3dRepository: X3dRepository,
    private val errorController: ApiErrorController
): EmojiUseCase {

    private val _emojiListState = MutableStateFlow<List<EmojiDto>>(emptyList())
    override val emojiListState: StateFlow<List<EmojiDto>>
        get() = _emojiListState.asStateFlow()

    override suspend fun fetchEmojiList(numInt: Int) {
        try{
            val emojiList = emojiRepository.fetchEmojiList(numInt)
            _emojiListState.emit(emojiList)
            Log.d("Fetch_E_L", "USECASE DONE: $emojiList")
        } catch (e: Exception) {
            errorController.setErrorState(-1)
        }
    }

    override suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji>? {
        return x3dRepository.createEmoji(videoUri, topK)
    }

    override suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        val dto = UploadEmojiDto(emojiUnicode, emojiLabel)
        return emojiRepository.uploadEmoji(videoFile, dto)
    }

    override suspend fun saveEmoji(id: String): Boolean {
        val response = emojiRepository.saveEmoji(id)
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
        val response = emojiRepository.unSaveEmoji(id)
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