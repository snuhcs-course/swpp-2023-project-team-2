package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import com.goliath.emojihub.models.Emoji
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.ui.theme.Color.Black
import com.goliath.emojihub.ui.theme.Color.White

fun getEmoji(unicode: Int): String {
    return String(Character.toChars(unicode))
}

@Composable
fun EmojiCell (
    emoji: Emoji,
    onSelected: (Emoji) -> Unit
) {
    Card (
        modifier = Modifier.fillMaxWidth().height(292.dp).clickable { onSelected(emoji) },
        shape = RoundedCornerShape(4.dp),
        elevation = 0.dp
    ) {
        // TODO: Add video thumbnail
        Box(Modifier.fillMaxSize().background(Black).alpha(0.25F))

        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "@" + emoji.createdBy,
                modifier = Modifier.align(Alignment.TopStart),
                fontSize = 12.sp,
                color = White
            )

            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.size(16.dp),
                    onClick = {
                        // TODO: save emoji
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.SaveAlt,
                        contentDescription = "",
                        tint = White
                    )
                }

                Text(
                    text = emoji.savedCount.toString(),
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 12.sp,
                    color = White
                )
            }
        }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = getEmoji(emoji.unicode.substring(2).toInt(16)),
                fontSize = 44.sp
            )
        }
    }
}