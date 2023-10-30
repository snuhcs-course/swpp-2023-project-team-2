package com.goliath.emojihub.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.usecases.EmojiUseCaseImpl_Factory
import com.goliath.emojihub.viewmodels.EmojiViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransformVideoPage(
    viewModel: EmojiViewModel,
) {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(viewModel.videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
        }
    }

    var transforming by remember { mutableStateOf(false) }
    var resultEmoji by remember { mutableStateOf<Pair<String, String>?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // TODO: implement
                            // show progress bar
                            transforming = true

                            resultEmoji = EmojiUseCase.createEmoji(viewModel.videoUri)
                            if (resultEmoji != null) transforming = false
                        },
                        enabled = !transforming
                    ) {
                        Text(text = if (resultEmoji != null) "업러드" else "변환", color = Color.Black)
                    }
                }
            )
        }
    ) { it ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer
                    }
                },
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )

            if (transforming) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            if (resultEmoji != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text (
                        text = String(Character.toChars(resultEmoji!!.first.substring(2).toInt(16))),
                        fontSize = 48.sp
                    )
                    Text (
                        text = "완료되었습니다",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }

    }
}