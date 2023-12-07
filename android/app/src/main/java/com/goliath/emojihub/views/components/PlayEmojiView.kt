package com.goliath.emojihub.views.components

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.models.User
import com.goliath.emojihub.models.UserDetails
import com.goliath.emojihub.navigateAsOrigin
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.UserViewModel

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PlayEmojiView(
    emojiViewModel: EmojiViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val currentEmoji = emojiViewModel.currentEmoji
    val currentUser = userViewModel.userState.collectAsState().value
    val currentUserDetails = userViewModel.userDetailsState.collectAsState().value

    var savedCount by remember { mutableIntStateOf(currentEmoji.savedCount) }
    var isSavedEmoji by remember { mutableStateOf(checkEmojiHasSaved(currentUserDetails, currentEmoji)) }
    val isCreatedEmoji by remember { mutableStateOf(checkEmojiHasCreated(currentUser, currentEmoji)) }

    val isSaveSuccess = emojiViewModel.saveEmojiState.asLiveData()
    val isUnSaveSuccess = emojiViewModel.unSaveEmojiState.asLiveData()
    var showNonUserDialog by remember { mutableStateOf(false) }
    var showUnSaveDialog by remember { mutableStateOf(false) }
    var showCreatedEmojiDialog by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(currentEmoji.videoLink))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
            userViewModel.fetchMyInfo()
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
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    player = exoPlayer
                }
            }
        )

        TopNavigationBar(
            title = "@" + currentEmoji.createdBy,
            largeTitle = false,
            needsElevation = false,
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
                            when {
                                currentUser == null -> showNonUserDialog = true
                                isSavedEmoji -> showUnSaveDialog = true
                                isCreatedEmoji -> showCreatedEmojiDialog = true
                                else -> emojiViewModel.saveEmoji(currentEmoji.id)
                            }
                        }
                    ) {
                        Icon(
                            imageVector =
                            if (isSavedEmoji) {
                                Icons.Default.FileDownloadOff
                            } else {
                                Icons.Default.FileDownload
                            },
                            tint = Color.White,
                            contentDescription = ""
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = savedCount.toString(),
                        fontSize = 14.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = currentEmoji.unicode.toEmoji(),
                        modifier = Modifier
                            .drawBehind {
                                drawCircle(color = Color.White)
                            }
                            .padding(10.dp),
                        fontSize = 28.sp,
                    )
                }
                Spacer(modifier = Modifier.padding(bottom = 32.dp))
            }
        }

        val saveEmojiStateObserver = Observer<Int> {
            if (it == 1) {
                isSavedEmoji = true
                savedCount++
                Toast.makeText(context, "Emoji saved!", Toast.LENGTH_SHORT).show()
            } else if (it == 0) {
                Toast.makeText(context, "Emoji save failed!", Toast.LENGTH_SHORT).show()
            }
            emojiViewModel.resetSaveEmojiState()
        }

        val unSaveEmojiStateObserver = Observer<Int> {
            if (it == 1) {
                isSavedEmoji = false
                savedCount--
                showUnSaveDialog = false
                Toast.makeText(context, "Emoji unsaved!", Toast.LENGTH_SHORT).show()
            } else if (it == 0) {
                showUnSaveDialog = false
                Toast.makeText(context, "Emoji unsave failed!", Toast.LENGTH_SHORT).show()
            }
            emojiViewModel.resetUnSaveEmojiState()
        }

        isSaveSuccess.observe(navController.currentBackStackEntry!!, saveEmojiStateObserver)
        isUnSaveSuccess.observe(navController.currentBackStackEntry!!, unSaveEmojiStateObserver)

        if (showNonUserDialog) {
            CustomDialog(
                title = "비회원 모드",
                body = "회원만 이모지를 저장할 수 있습니다. 로그인 화면으로 이동할까요?",
                confirmText = "이동",
                needsCancelButton = true,
                onDismissRequest = { showNonUserDialog = false },
                dismiss = { showNonUserDialog = false },
                confirm = {
                    navController.navigateAsOrigin(NavigationDestination.Onboard)
                }
            )
        }
        
        if (showUnSaveDialog) {
            CustomDialog(
                title = "삭제",
                body = "저장된 이모지에서 삭제하시겠습니까?",
                confirmText = "삭제",
                isDestructive = true,
                needsCancelButton = true,
                onDismissRequest = { showUnSaveDialog = false },
                confirm = { emojiViewModel.unSaveEmoji(currentEmoji.id) },
                dismiss = { showUnSaveDialog = false }
            )
        }

        if (showCreatedEmojiDialog) {
            CustomDialog(
                title = "내가 만든 이모지",
                body = "내가 만든 이모지는 저장할 수 없습니다.",
                confirmText = "확인",
                needsCancelButton = false,
                onDismissRequest = { showCreatedEmojiDialog = false },
                confirm = { showCreatedEmojiDialog = false }
            )
        }
    }
}

fun checkEmojiHasSaved(currentUserDetails: UserDetails?, currentEmoji: Emoji): Boolean {
    if (currentUserDetails == null) return false
    Log.d("checkEmojiHasSaved", "currentUserDetails.savedEmojiList: ${currentUserDetails.savedEmojiList}")
    Log.d("checkEmojiHasSaved", "currentEmoji.id: ${currentEmoji.id}")
    return currentUserDetails.savedEmojiList?.contains(currentEmoji.id) == true
}

fun checkEmojiHasCreated(currentUser: User?, currentEmoji: Emoji): Boolean {
    if (currentUser == null) return false
    return currentUser.name == currentEmoji.createdBy
}