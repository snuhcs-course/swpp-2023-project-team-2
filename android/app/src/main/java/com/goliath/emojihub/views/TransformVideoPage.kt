package com.goliath.emojihub.views

import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    var currentEmojiIndex by remember { mutableStateOf(0) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(viewModel.videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
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
                                    createdEmojiList = viewModel.createEmoji(viewModel.videoUri)
                                    Log.d("TransformVideoPage", "createdEmojis: $createdEmojiList")
                                }
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
                                        createdEmojiList[0].emojiUnicode,
                                        createdEmojiList[0].emojiClassName,
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

            if (createdEmojiList.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "영상과 가장 잘 어울리는 이모지를 골라주세요",
                        color = com.goliath.emojihub.ui.theme.Color.White,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = Color.Gray.copy(alpha = 0.3f), // Adjust alpha for transparency
                                shape = RoundedCornerShape(12.dp) // Adjust corner size for roundness
                            )
                    ) {
                        Text(
                            text = createdEmojiList[currentEmojiIndex].emojiUnicode.toEmoji(),
                            fontSize = 48.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(onClick = {
                            currentEmojiIndex = (currentEmojiIndex - 1 + createdEmojiList.size) % createdEmojiList.size
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Previous emoji",
                            )
                        }
                        Text(
                            text = createdEmojiList[currentEmojiIndex].emojiClassName,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        IconButton(onClick = {
                            currentEmojiIndex = (currentEmojiIndex + 1) % createdEmojiList.size
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next emoji"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Handle emoji selection
                        }
                    ) {
                        Text(text = "이모지 선택")
                    }
//                    Text (
//                        text = createdEmojiList[0].emojiClassName,
//                        fontSize = 48.sp
//                    )
//                    Text (
//                        text = "완료되었습니다",
//                        fontSize = 24.sp,
//                    )
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