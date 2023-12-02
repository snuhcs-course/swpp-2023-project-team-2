package com.goliath.emojihub.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.LightGray
import com.goliath.emojihub.ui.theme.Color.Transparent

@Composable
fun EmojiClassFilterRow(
    emojiClass: List<String>,
    emojiCounts: Map<String, Int>,
    onEmojiClassSelected: (String) -> Unit,
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()

    // Display a row of buttons for each emoji class
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        content()
    }
}

@Composable
fun EmojiClassFilterButton(
    text: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
   TextButton(
       onClick = onSelected,
       modifier = Modifier
           .padding(4.dp),
       colors = ButtonDefaults.textButtonColors(
              backgroundColor = if (isSelected) LightGray else Transparent,
              contentColor = Color.Black,
         )
       )
    {
       Text(
           text = text,
           fontWeight = FontWeight.Bold,
       )
   }
}
