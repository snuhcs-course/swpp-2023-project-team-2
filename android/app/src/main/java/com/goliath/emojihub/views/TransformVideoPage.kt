package com.goliath.emojihub.views

import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.views.components.CustomDialog
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransformVideoPage(
    viewModel: EmojiViewModel,
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(viewModel.videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
        }
    }

    var resultEmoji by remember { mutableStateOf<List<CreatedEmoji>?>(null) }

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
                            if (resultEmoji == null) {
                                resultEmoji = viewModel.createEmoji(viewModel.videoUri)
                                Log.d("TransformVideoPage", "resultEmoji: $resultEmoji")
                            }
                            else {
                                var realPath: String? = null
                                // Query to get the actual file path
                                val projection = arrayOf(MediaStore.Images.Media.DATA)
                                val cursor = context.contentResolver.query(
                                    viewModel.videoUri, projection, null, null, null
                                )

                                cursor?.use {
                                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                                    it.moveToFirst()
                                    realPath = it.getString(columnIndex)
                                }

                                val videoFile = File(realPath)
                                Log.d("TransformVideoPage", "videoPath: $realPath")
                                coroutineScope.launch {
                                    // FIXME: add choose emoji dialog from topK emojis
                                    val success = viewModel.uploadEmoji(
                                        resultEmoji!![0].emojiUnicode, resultEmoji!![0].emojiClassName, videoFile
                                    )
                                    Log.d("TransformVideoPage", "success: $success")
                                    if (success) {
                                        showSuccessDialog = true
                                    }
                                }
                            }
                        },
                    ) {
                        Text(text = if (resultEmoji != null) "업로드" else "변환", color = Color.Black)
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

            if (resultEmoji != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = resultEmoji!![0].emojiUnicode.toEmoji(),
                        fontSize = 48.sp
                    )
                    Text (
                        text = resultEmoji!![0].emojiClassName,
                        fontSize = 48.sp
                    )
                    Text (
                        text = "완료되었습니다",
                        fontSize = 24.sp,
                    )
                }
            }

            if (showSuccessDialog) {
                CustomDialog(
                    title = "완료",
                    body = "동영상 업로드가 완료되었습니다.",
                    confirm = { navController.popBackStack() }
                )
            }
        }
    }
}