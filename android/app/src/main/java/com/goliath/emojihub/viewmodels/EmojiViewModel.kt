package com.goliath.emojihub.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.usecases.EmojiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val emojiUseCase: EmojiUseCase
): ViewModel() {
    var videoUri: Uri = Uri.EMPTY
    var currentEmoji: Emoji? = null
    var isBottomSheetShown by mutableStateOf(false)

    private val _emojiList = MutableStateFlow<List<Emoji>>(emptyList())
    val emojiList: StateFlow<List<Emoji>> = _emojiList.asStateFlow()

    fun fetchEmojiList(numInt: Int)
    {
        viewModelScope.launch {
            emojiUseCase.fetchEmojiList(numInt)

            val emojis = emojiUseCase.emojiListState.value.map { dto -> Emoji(dto) }
            _emojiList.emit(emojis)
            Log.d("Fetch_E_L", "VIEWMODEL DONE: $emojis")
        }
    }

    fun createEmoji(videoUri: Uri): List<CreatedEmoji>? {
        return emojiUseCase.createEmoji(videoUri)
    }

    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        return emojiUseCase.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
    }

    suspend fun saveEmoji(id: String) {
        emojiUseCase.saveEmoji(id)
    }

    suspend fun unSaveEmoji(id: String) {
        emojiUseCase.saveEmoji(id)
    }
}