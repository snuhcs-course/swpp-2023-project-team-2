package com.goliath.emojihub.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.usecases.EmojiUseCase
import com.goliath.emojihub.views.components.BottomSheetContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val emojiUseCase: EmojiUseCase
): ViewModel() {
    lateinit var videoUri: Uri
    lateinit var currentEmoji: Emoji
    var bottomSheetContent by mutableStateOf(BottomSheetContent.EMPTY)

    private val _saveEmojiState = MutableStateFlow<Result<Unit>?>(null)
    val saveEmojiState = _saveEmojiState.asStateFlow()

    private val _unSaveEmojiState = MutableStateFlow<Result<Unit>?>(null)
    val unSaveEmojiState = _unSaveEmojiState.asStateFlow()

    val emojiList = emojiUseCase.emojiList
    val myCreatedEmojiList = emojiUseCase.myCreatedEmojiList
    val mySavedEmojiList = emojiUseCase.mySavedEmojiList

    companion object {
        private const val _topK = 3
    }

    fun fetchEmojiList() {
        viewModelScope.launch {
            emojiUseCase.fetchEmojiList()
                .cachedIn(viewModelScope)
                .collect {
                    emojiUseCase.updateEmojiList(it)
                }
        }
    }

    fun fetchMyCreatedEmojiList() {
        viewModelScope.launch {
            emojiUseCase.fetchMyCreatedEmojiList()
                .cachedIn(viewModelScope)
                .collect {
                    emojiUseCase.updateMyCreatedEmojiList(it)
                }
        }
    }

    fun fetchMySavedEmojiList() {
        viewModelScope.launch {
            emojiUseCase.fetchMySavedEmojiList()
                .cachedIn(viewModelScope)
                .collect {
                    emojiUseCase.updateMySavedEmojiList(it)
                }
        }
    }

    suspend fun createEmoji(videoUri: Uri): List<CreatedEmoji> {
        return withContext(Dispatchers.IO) {
            val createdEmojiList = emojiUseCase.createEmoji(videoUri, _topK)
            Log.d("EmojiViewModel", "Done create emoji: $createdEmojiList")
            createdEmojiList
        }
    }

    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        return emojiUseCase.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
    }

    fun saveEmoji(id: String) {
        viewModelScope.launch {
            val result = emojiUseCase.saveEmoji(id)
            _saveEmojiState.value = result
        }
    }

    fun unSaveEmoji(id: String) {
        viewModelScope.launch {
            val result = emojiUseCase.unSaveEmoji(id)
            _unSaveEmojiState.value = result
        }
    }
}