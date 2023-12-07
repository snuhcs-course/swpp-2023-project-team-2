package com.goliath.emojihub.usecases

import android.net.Uri
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.UploadEmojiDto
import com.goliath.emojihub.repositories.local.X3dRepository
import com.goliath.emojihub.repositories.remote.EmojiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.IOException
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiUseCase {
    val emojiList: StateFlow<PagingData<Emoji>>
    val myCreatedEmojiList: StateFlow<PagingData<Emoji>>
    val mySavedEmojiList: StateFlow<PagingData<Emoji>>
    suspend fun updateEmojiList(data: PagingData<Emoji>)
    suspend fun updateMyCreatedEmojiList(data: PagingData<Emoji>)
    suspend fun updateMySavedEmojiList(data: PagingData<Emoji>)
    suspend fun fetchEmojiList(sortByDate: Int): Flow<PagingData<Emoji>>
    suspend fun fetchMyCreatedEmojiList(): Flow<PagingData<Emoji>>
    suspend fun fetchMySavedEmojiList(): Flow<PagingData<Emoji>>
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

    private val _myCreatedEmojiList = MutableStateFlow<PagingData<Emoji>>(PagingData.empty())
    override val myCreatedEmojiList: StateFlow<PagingData<Emoji>>
        get() = _myCreatedEmojiList

    private val _mySavedEmojiList = MutableStateFlow<PagingData<Emoji>>(PagingData.empty())
    override val mySavedEmojiList: StateFlow<PagingData<Emoji>>
        get() = _mySavedEmojiList

    override suspend fun updateEmojiList(data: PagingData<Emoji>) {
        _emojiList.emit(data)
    }

    override suspend fun updateMyCreatedEmojiList(data: PagingData<Emoji>) {
        _myCreatedEmojiList.emit(data)
    }

    override suspend fun updateMySavedEmojiList(data: PagingData<Emoji>) {
        _mySavedEmojiList.emit(data)
    }

    override suspend fun fetchEmojiList(sortByDate: Int): Flow<PagingData<Emoji>> {
        return try {
            emojiRepository.fetchEmojiList(sortByDate).map { it.map { dto -> Emoji(dto) } }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            flowOf(PagingData.empty())
        } catch (e: Exception) {
            Log.e("EmojiUseCase", "Unknown Exception on fetchMyEmojiList: ${e.message}")
            flowOf(PagingData.empty())
        }
    }

    override suspend fun fetchMyCreatedEmojiList(): Flow<PagingData<Emoji>> {
        return try {
            emojiRepository.fetchMyCreatedEmojiList().map { it.map { dto -> Emoji(dto) } }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            flowOf(PagingData.empty())
        } catch (e: Exception) {
            Log.e("EmojiUseCase", "Unknown Exception on fetchMyCreatedEmojiList: ${e.message}")
            flowOf(PagingData.empty())
        }
    }

    override suspend fun fetchMySavedEmojiList(): Flow<PagingData<Emoji>> {
        return try {
            emojiRepository.fetchMySavedEmojiList().map { it.map { dto -> Emoji(dto) } }
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            flowOf(PagingData.empty())
        } catch (e: Exception) {
            Log.e("EmojiUseCase", "Unknown Exception on fetchMySavedEmojiList: ${e.message}")
            flowOf(PagingData.empty())
        }
    }

    override suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji> {
        return try {
            x3dRepository.createEmoji(videoUri, topK)
        } catch (e: Exception) {
            Log.e("EmojiUseCase", "Unknown Exception on createEmoji: ${e.message}")
            x3dRepository.DEFAULT_EMOJI_LIST
        }
    }

    override suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        val dto = UploadEmojiDto(emojiUnicode, emojiLabel)
        return try {
            val response = emojiRepository.uploadEmoji(videoFile, dto)
            if (response.isSuccessful) {
                true
            } else {
                errorController.setErrorState(response.code())
                false
            }
        } catch (e: IOException) {
            Log.e("EmojiUseCase", "IOException")
            false
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            false
        } catch (e: Exception) {
            Log.e("EmojiUseCase", "Unknown Exception on uploadEmoji: ${e.message}")
            false
        }
    }

    override suspend fun saveEmoji(id: String): Boolean {
        return try {
            emojiRepository.saveEmoji(id).isSuccessful
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            false
        } catch (e: Exception) {
            Log.e("EmojiUseCase", "Unknown Exception on saveEmoji: ${e.message}")
            false
        }
    }

    override suspend fun unSaveEmoji(id: String): Boolean {
        return try {
            emojiRepository.unSaveEmoji(id).isSuccessful
        } catch (e: ConnectException) {
            errorController.setErrorState(CustomError.INTERNAL_SERVER_ERROR.statusCode)
            false
        } catch (e: Exception) {
            Log.e("EmojiUseCase", "Unknown Exception on unSaveEmoji: ${e.message}")
            false
        }
    }
}