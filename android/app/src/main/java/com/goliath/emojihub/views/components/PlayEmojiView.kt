package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.EmojiViewModel
import kotlinx.coroutines.launch

@Composable
fun PlayEmojiView(
    viewModel: EmojiViewModel
) {
    // Play video
    val context = LocalContext.current
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(viewModel.currentEmoji!!.videoLink))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)),
                shape = RectangleShape,
                alpha = 0.25F
            )
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize().background(Color.EmojiHubRed),
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                }
            }
        )

        TopNavigationBar(
            title = "@" + viewModel.currentEmoji!!.createdBy,
            largeTitle = false,
            navigate = { navController.popBackStack() }
        ) {}

        Row(Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))

            Column(Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            coroutineScope.launch { viewModel.saveEmoji(viewModel.currentEmoji!!.id) }
                        }
                    ) {
                        Icon(
                            imageVector =
                            if (viewModel.currentEmoji!!.isSaved) {
                                Icons.Default.FileDownloadOff
                            } else {
                                Icons.Default.FileDownload },
                            tint = Color.White,
                            contentDescription = ""
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = viewModel.currentEmoji!!.savedCount.toString(),
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = viewModel.currentEmoji!!.unicode.toEmoji(),
                        modifier = Modifier.drawBehind {
                            drawCircle(color = Color.White)
                        }.padding(10.dp),
                        fontSize = 28.sp,
                    )
                }

                Spacer(modifier = Modifier.padding(bottom = 32.dp))
            }
        }
    }
}