package com.goliath.emojihub.viewmodels

import com.goliath.emojihub.usecases.EmojiUseCase
import io.mockk.spyk
import org.junit.Assert.*

import org.junit.Test

class EmojiViewModelTest {
    private val emojiUseCase = spyk<EmojiUseCase>()
    private val emojiViewModel = EmojiViewModel(emojiUseCase)

    @Test
    fun fetchEmojiList() {
    }

    @Test
    fun createEmoji() {
    }

    @Test
    fun uploadEmoji() {
    }

    @Test
    fun saveEmoji() {
    }

    @Test
    fun unSaveEmoji() {
    }
}