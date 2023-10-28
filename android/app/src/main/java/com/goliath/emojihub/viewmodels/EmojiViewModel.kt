package com.goliath.emojihub.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.goliath.emojihub.usecases.EmojiUseCase
import com.goliath.emojihub.views.PageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val emojiUseCase: EmojiUseCase
): ViewModel() {
    private val _emojiState = MutableStateFlow<PageItem.Emoji?>(null)
    val emojiState = _emojiState.asStateFlow()

    var videoUri: Uri = Uri.EMPTY
}