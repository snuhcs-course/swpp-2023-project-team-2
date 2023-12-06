package com.goliath.emojihub.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.extensions.reactionsToString
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.navigateAsOrigin
import com.goliath.emojihub.ui.theme.Color.EmojiHubDetailLabel
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun PostCell(
    post: Post,
    isNonUser: Boolean = false
) {
    val navController = LocalNavController.current
    val bottomSheetState = LocalBottomSheetController.current
    val coroutineScope = rememberCoroutineScope()
    val emojiViewModel = hiltViewModel<EmojiViewModel>()
    val postViewModel = hiltViewModel<PostViewModel>()

    var showNonUserDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "@" + post.createdBy,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = post.createdAt,
                    fontSize = 12.sp,
                    color = EmojiHubDetailLabel
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = post.content,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (post.reaction.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                emojiViewModel.bottomSheetContent = BottomSheetContent.VIEW_REACTION
                                postViewModel.currentPostId = post.id
                                postViewModel.currentPost = post
                                bottomSheetState.show()
                            }
                        },

                        ) {
                        Text(
                            text = reactionsToString(post.reaction),
                            fontSize = 13.sp,
                            color = EmojiHubDetailLabel
                        )
                    }
                } else { //TODO: Find a better way to not display anything
                    Text(
                        text = "",
                        fontSize = 13.sp,
                        color = EmojiHubDetailLabel
                    )
                }

                IconButton(onClick = {
                    if (isNonUser) {
                        showNonUserDialog = true
                    } else {
                        coroutineScope.launch {
                            emojiViewModel.bottomSheetContent = BottomSheetContent.ADD_REACTION
                            postViewModel.currentPostId = post.id
                            postViewModel.currentPost = post
                            bottomSheetState.show()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.AddReaction,
                        contentDescription = ""
                    )
                }
            }
        }

        if (showNonUserDialog) {
            CustomDialog(
                title = "비회원 모드",
                body = "회원만 게시물에 반응을 남길 수 있습니다. 로그인 화면으로 이동할까요?",
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