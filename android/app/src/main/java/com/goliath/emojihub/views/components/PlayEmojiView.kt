package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileDownloadOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.ui.theme.Color

@Composable
fun PlayEmojiView(
    emoji: Emoji,
    popBackStack: () -> Unit,
    saveEmoji: () -> Unit
) {
    // Play video
    

    // Gradient background
    Box(
        Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)),
                shape = RectangleShape,
                alpha = 0.25F
            )
    )

    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp))
    {
        Column(horizontalAlignment = Alignment.End) {
            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        saveEmoji()
                    }
                ) {
                    Icon(
                        imageVector =
                        if (emoji.isSaved) {
                            Icons.Default.FileDownloadOff
                        } else {
                            Icons.Default.FileDownload },
                        tint = Color.White,
                        contentDescription = ""
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = emoji.savedCount.toString(),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = getEmoji(emoji.unicode.substring(2).toInt(16)),
                    modifier = Modifier.padding(10.dp).drawBehind {
                        drawCircle(color = Color.White)
                    },
                    fontSize = 32.sp,
                )
            }

            Spacer(modifier = Modifier.padding(bottom = 32.dp))
        }
    }

    TopNavigationBar(
        title = "@" + emoji.createdBy,
        largeTitle = false,
        navigate = { popBackStack() }
    ) {}
}