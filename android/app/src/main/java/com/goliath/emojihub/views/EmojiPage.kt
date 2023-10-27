package com.goliath.emojihub.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.views.components.EmojiCell

@Composable
fun EmojiPage(
    emojiList: List<Emoji>
) {
//    Text(text = "Emoji")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(emojiList.size) {index ->
             EmojiCell(emoji = emojiList[index])
        }

    }
}