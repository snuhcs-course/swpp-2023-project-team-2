package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.ui.theme.Color

@Composable
fun SavedEmojiListView(

) {
    val navController = LocalNavController.current

    Column (
        Modifier.background(Color.White)
    ) {
        TopNavigationBar(
            title = "저장된 이모지",
            navigate = { navController.popBackStack() }
        )
    }
}