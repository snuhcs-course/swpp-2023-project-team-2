package com.goliath.emojihub.usecases

import android.net.Uri
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.UploadEmojiDto
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    val emojiList: StateFlow<PagingData<Emoji>>
    suspend fun updateEmojiList(data: PagingData<Emoji>)
    suspend fun fetchEmojiList(): Flow<PagingData<Emoji>>
    suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji>
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

    private val _emojiList = MutableStateFlow<PagingData<Emoji>>(PagingData.empty())
    override val emojiList: StateFlow<PagingData<Emoji>>
        get() = _emojiList

    override suspend fun updateEmojiList(data: PagingData<Emoji>) {
        _emojiList.emit(data)
    }
    override suspend fun fetchEmojiList(): Flow<PagingData<Emoji>> {
        return emojiRepository.fetchEmojiList().map { it.map { dto -> Emoji(dto) } }
    }

    override suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji> {
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