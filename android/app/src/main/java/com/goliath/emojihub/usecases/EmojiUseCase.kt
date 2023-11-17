package com.goliath.emojihub.usecases

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.content.ContentResolver
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.RequiresApi
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.EmojiDto

import com.goliath.emojihub.models.UploadEmojiDto
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    val emojiListState: StateFlow<List<EmojiDto>>
    suspend fun fetchEmojiList(numInt: Int)
    fun createEmoji(videoUri: Uri): Pair<String, String>?

    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean
    suspend fun saveEmoji(id: String): Boolean
    suspend fun unSaveEmoji(id: String): Boolean

    suspend fun createVideoThumbnail(videoUri: String, width: Int, height: Int): Bitmap?
}

@Singleton
class EmojiUseCaseImpl @Inject constructor(
    private val repository: EmojiRepository,
    private val model: X3dRepository,
    private val errorController: ApiErrorController
): EmojiUseCase {

    private val _emojiListState = MutableStateFlow<List<EmojiDto>>(emptyList())
    override val emojiListState: StateFlow<List<EmojiDto>>
        get() = _emojiListState.asStateFlow()

    override suspend fun fetchEmojiList(numInt: Int) {
        try{
            val emojiList = repository.fetchEmojiList(numInt)
            _emojiListState.emit(emojiList)
        } catch (e: Exception) {
            errorController.setErrorState(-1)
        }
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

    override suspend fun createVideoThumbnail(videoUri: String, width: Int, height: Int): Bitmap? {
        val retriever = MediaMetadataRetriever()

        try {
            Log.d("create_TN", "Created : ${videoUri}")
            retriever.setDataSource(videoUri, HashMap<String, String>())
            return retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            Log.d("create_TN", "Fail to Create")
            errorController.setErrorState(-1)
        } finally {
            try{
                retriever.release()
            } catch (e: Exception) {
                errorController.setErrorState(-1)
            }
        }
        return null
    }
}