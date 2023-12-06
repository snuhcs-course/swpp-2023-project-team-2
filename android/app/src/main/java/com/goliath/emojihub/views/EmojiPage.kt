package com.goliath.emojihub.views

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.navigateAsOrigin
import com.goliath.emojihub.ui.theme.Color.Black
import com.goliath.emojihub.ui.theme.Color.LightGray
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.components.CustomDialog
import com.goliath.emojihub.views.components.EmojiCell
import com.goliath.emojihub.views.components.EmojiCellDisplay
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun EmojiPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val userViewModel = hiltViewModel<UserViewModel>()
    val emojiViewModel = hiltViewModel<EmojiViewModel>()

    val currentUser = userViewModel.userState.collectAsState().value
    val emojiList = emojiViewModel.emojiList.collectAsLazyPagingItems()

    var showNonUserDialog by remember { mutableStateOf(false) }
    var showVideoTooLongDialog by remember { mutableStateOf(false) }
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val duration = retriever.extractMetadata(METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
            retriever.release()
            if (duration >= 6000) {
                showVideoTooLongDialog = true
            } else {
                emojiViewModel.videoUri = uri
                navController.navigate(NavigationDestination.TransformVideo)
            }
        }
    }

    LaunchedEffect(Unit) {
        emojiViewModel.fetchEmojiList()
    }

    Column(Modifier.background(White)) {
        TopNavigationBar("Emoji", shouldNavigate = false) {
            IconButton(onClick = {
                if (currentUser == null) {
                    showNonUserDialog = true
                } else {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.READ_MEDIA_VIDEO
                        ) -> {
                            pickMediaLauncher.launch(PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.VideoOnly
                            ))
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                        }
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = ""
                )
            }
        }

        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (emojiViewModel.sortByDate == 0) "Trending 🔥" else "Recently added " + "U+D83D U+DD52".toEmoji(), fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Column {
                    Button(
                        onClick = { dropDownMenuExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Black,
                            contentColor = White
                        )
                    ) {
                        Text(text = "Sort by", fontSize = 12.sp)
                    }

                    DropdownMenu(
                        expanded = dropDownMenuExpanded,
                        onDismissRequest = { dropDownMenuExpanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            emojiViewModel.sortByDate = 1
                            emojiViewModel.fetchEmojiList()
                            dropDownMenuExpanded = false
                        }) {
                            Text(text = "created date")
                        }
                        DropdownMenuItem(onClick = {
                            emojiViewModel.sortByDate = 0
                            emojiViewModel.fetchEmojiList()
                            dropDownMenuExpanded = false
                        }) {
                            Text(text = "save count")
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(emojiList.itemCount) { index ->
                    emojiList[index]?.let{
                        EmojiCell(emoji = it, displayMode = EmojiCellDisplay.VERTICAL) { selectedEmoji ->
                            emojiViewModel.currentEmoji = selectedEmoji
                            navController.navigate(NavigationDestination.PlayEmojiVideo)
                        }
                    }
                }
            }
        }

        if (showVideoTooLongDialog) {
            CustomDialog(
                title = "안내",
                body = "최대 5초 길이의 영상만 업로드할 수 있습니다.",
                onDismissRequest = { showVideoTooLongDialog = false },
                confirm = { showVideoTooLongDialog = false }
            )
        }

        if (showNonUserDialog) {
            CustomDialog(
                title = "비회원 모드",
                body = "회원만 이모지를 생성할 수 있습니다. 로그인 화면으로 이동할까요?",
                confirmText = "이동",
                needsCancelButton = true,
                onDismissRequest = { showNonUserDialog = false },
                dismiss = { showNonUserDialog = false },
                confirm = {
                    navController.navigateAsOrigin(NavigationDestination.Onboard)
                }
            )
        }
    }
}