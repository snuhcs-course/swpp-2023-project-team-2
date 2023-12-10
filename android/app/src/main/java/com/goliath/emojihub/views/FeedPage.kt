package com.goliath.emojihub.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.navigateAsOrigin
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.EmojiHubDividerColor
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.PostViewModel
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.components.CustomBottomSheet
import com.goliath.emojihub.views.components.CustomDialog
import com.goliath.emojihub.views.components.PostCell
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun FeedPage() {
    val navController = LocalNavController.current
    val bottomSheetController = LocalBottomSheetController.current

    val emojiViewModel = hiltViewModel<EmojiViewModel>()
    val postViewModel = hiltViewModel<PostViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()

    val currentUser = userViewModel.userState.collectAsState().value

    val postList = postViewModel.postList.collectAsLazyPagingItems()

    var showNonUserDialog by remember { mutableStateOf(false) }

    // 앱이 처음 실행될 때, 유저 정보를 가져오기 위함
    LaunchedEffect(userViewModel) {
        userViewModel.fetchMyInfo()
    }

    LaunchedEffect(Unit) {
        postViewModel.fetchPostList()
    }

    Column (
        Modifier.background(Color.White)
    ) {
        TopNavigationBar("Feed", shouldNavigate = false) {
            IconButton(onClick = {
                if (currentUser == null) {
                    showNonUserDialog = true
                } else {
                    navController.navigate(NavigationDestination.CreatePost)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = ""
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(postList.itemCount) { index ->
                    postList[index]?.let {
                        PostCell(post = it, isNonUser = currentUser == null)
                        Divider(color = EmojiHubDividerColor, thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    if (showNonUserDialog) {
        CustomDialog(
            title = "비회원 모드",
            body = "회원만 글을 작성할 수 있습니다. 로그인 화면으로 이동할까요?",
            confirmText = "이동",
            needsCancelButton = true,
            onDismissRequest = { showNonUserDialog = false },
            dismiss = { showNonUserDialog = false },
            confirm = {
                navController.navigateAsOrigin(NavigationDestination.Onboard)
            }
        )
    }

    if (bottomSheetController.isVisible) {
        CustomBottomSheet(
            bottomSheetContent = emojiViewModel.bottomSheetContent
        )
    }
}