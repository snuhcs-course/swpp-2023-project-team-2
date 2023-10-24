package com.goliath.emojihub.views.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.ui.theme.Color.EmojiHubGrayIcon
import com.goliath.emojihub.ui.theme.Color.EmojiHubRed

@Composable
fun ProfileMenuCell(
    label: String,
    needsTrailingButton: Boolean = false,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (isDestructive) EmojiHubRed else Color.Black
        )
        if (needsTrailingButton) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = null,
                modifier = Modifier.size(size = 24.dp),
                tint = EmojiHubGrayIcon
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileMenuCellPreview() {
    ProfileMenuCell(label = "내가 만든 이모지", needsTrailingButton = true) {}
}