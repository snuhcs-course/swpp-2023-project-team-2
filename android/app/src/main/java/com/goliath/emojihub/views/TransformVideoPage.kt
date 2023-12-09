package com.goliath.emojihub.views

import android.annotation.SuppressLint
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import androidx.media3.ui.PlayerView
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.views.components.CustomDialog
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransformVideoPage(
    emojiViewModel: EmojiViewModel,
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var currentEmojiIndex by remember { mutableIntStateOf(0) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(emojiViewModel.videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    var createdEmojiList by remember { mutableStateOf<List<CreatedEmoji>>(emptyList()) }

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
                            if (createdEmojiList.isEmpty()) {
                                coroutineScope.launch {
                                    createdEmojiList = emojiViewModel.createEmoji(emojiViewModel.videoUri)
                                    Log.d("TransformVideoPage", "createdEmojis: $createdEmojiList")
                                }
                            }
                            else {
                                var realPath: String? = null
                                // Query to get the actual file path
                                val projection = arrayOf(MediaStore.Images.Media.DATA)
                                val cursor = context.contentResolver.query(
                                    emojiViewModel.videoUri, projection, null, null, null
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
                                    val success = emojiViewModel.uploadEmoji(
                                        createdEmojiList[currentEmojiIndex].emojiUnicode,
                                        createdEmojiList[currentEmojiIndex].emojiClassName,
                                        videoFile
                                    )
                                    Log.d("TransformVideoPage", "success: $success")
                                    if (success) {
                                        showSuccessDialog = true
                                    }
                                }
                            }
                        },
                    ) {
                        Text(text = if (createdEmojiList.isNotEmpty()) "업로드" else "변환", color = Color.Black)
                    }
                }
            )
        }
    ) { it ->
        Box(Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        setShowFastForwardButton(false)
                        setShowRewindButton(false)
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                        resizeMode = RESIZE_MODE_ZOOM
                        player = exoPlayer
                    }
                },
                modifier = Modifier.padding(it).fillMaxSize()
            )

            if (createdEmojiList.isNotEmpty()) {
                Box(Modifier.background(Color.Black.copy(alpha = 0.5f))) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 48.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "영상과 가장 잘 어울리는\n이모지를 골라주세요",
                            color = com.goliath.emojihub.ui.theme.Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ){
                            if (currentEmojiIndex > 0) {
                                IconButton(
                                    onClick = {
                                        currentEmojiIndex = (currentEmojiIndex - 1 + createdEmojiList.size) % createdEmojiList.size
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NavigateBefore,
                                        contentDescription = "Previous emoji",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.weight(1f).size(120.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Text(
                                    text = createdEmojiList[currentEmojiIndex].emojiUnicode.toEmoji(),
                                    fontSize = 60.sp
                                )
                            }

                            if (currentEmojiIndex < createdEmojiList.size - 1) {
                                IconButton(
                                    onClick = {
                                        currentEmojiIndex = (currentEmojiIndex + 1) % createdEmojiList.size
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NavigateNext,
                                        contentDescription = "Next emoji",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = createdEmojiList[currentEmojiIndex].emojiClassName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.White
                        )
                    }
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