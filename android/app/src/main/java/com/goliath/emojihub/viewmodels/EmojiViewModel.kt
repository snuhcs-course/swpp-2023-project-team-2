package com.goliath.emojihub.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.usecases.EmojiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val emojiUseCase: EmojiUseCase
): ViewModel() {
    var videoUri: Uri = Uri.EMPTY
    var currentEmoji: Emoji? = null
    var isBottomSheetShown by mutableStateOf(false)

    fun createEmoji(videoUri: Uri): Pair<String, String>? {
        val (emojiName, emojiUnicode) = emojiUseCase.createEmoji(videoUri)?: return null

        val emojiCharArray = emojiUnicode.replace("U+", "")
            .split(" ").map {
            val codePoint = it.toInt(16)
                codePoint.toChar()
        }.toCharArray()
        val emoji = String(emojiCharArray)
        return Pair(emojiName, emoji)
    }

    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        return emojiUseCase.uploadEmoji(emojiUnicode, emojiLabel, videoFile)
    }

    suspend fun saveEmoji(id: String) {
        emojiUseCase.saveEmoji(id)
    }
}