package com.goliath.emojihub.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.viewmodels.EmojiViewModel

@Composable
fun ProfilePage() {
    val emojiViewModel = hiltViewModel<EmojiViewModel>()

    Text(text = "Profile")
}