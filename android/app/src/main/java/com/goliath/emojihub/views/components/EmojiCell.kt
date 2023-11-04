package com.goliath.emojihub.views.components

import androidx.compose.runtime.Composable
import com.goliath.emojihub.models.Emoji
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.ui.theme.Color

fun getEmoji(unicode: Int): String {
    return String(Character.toChars(unicode))
}

@Composable
fun EmojiCell (
    emoji: Emoji,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(
            modifier = Modifier.height(292.dp)
        ) {
            // TODO: put dynamic emoji here (maybe put only thumbnail?)
            FloatingActionButton(
                modifier = Modifier.align(Alignment.Center),
                contentColor = Color.White,
                backgroundColor = Color.LightGray,
                onClick = {
                    //TODO: play video
                }
            ) {
                Icon(Icons.Filled.PlayCircle, "Play video button.")
            }

            Text( // creator username
                text = "@" + emoji.createdBy,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                fontSize = 16.sp,
                color = Color.Black
            )

            ExtendedFloatingActionButton( // Download button
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                backgroundColor = Color.White,
                contentColor = Color.Black,
                onClick = { //TODO: save DE
                    },
                icon = { Icon(Icons.Filled.SaveAlt, "Save DE button.") },
                text = { Text(text = emoji.savedCount.toString()) },
            )

            Text( // corresponding emoji
                text = getEmoji(emoji.unicode.substring(2).toInt(16)), // TODO: display emoji corresponding to the unicode
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                fontSize = 16.sp
            )
        }
    }
}


