package com.goliath.emojihub.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.navigateAsOrigin
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.EmojiHubDetailLabel
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.PostViewModel
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.components.CustomDialog
import com.goliath.emojihub.views.components.EmojiCell
import com.goliath.emojihub.views.components.EmojiCellDisplay
import com.goliath.emojihub.views.components.EmptyProfile
import com.goliath.emojihub.views.components.PreviewPostCell
import com.goliath.emojihub.views.components.ProfileMenuCell
import com.goliath.emojihub.views.components.ProfileMenuCellWithPreview
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun ProfilePage() {
    val navController = LocalNavController.current
    val scrollState = rememberScrollState()

    val userViewModel = hiltViewModel<UserViewModel>()
    val postViewModel = hiltViewModel<PostViewModel>()
    val emojiViewModel = hiltViewModel<EmojiViewModel>()

    val currentUser = userViewModel.userState.collectAsState().value

    val myPostList = postViewModel.myPostList.collectAsLazyPagingItems()
    val myCreatedEmojiList = emojiViewModel.myCreatedEmojiList.collectAsLazyPagingItems()
    val mySavedEmojiList = emojiViewModel.mySavedEmojiList.collectAsLazyPagingItems()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    // 앱이 처음 실행될 때, 유저 정보를 가져오기 위함
    LaunchedEffect(userViewModel) {
        userViewModel.fetchMyInfo()
    }

    LaunchedEffect(Unit) {
        postViewModel.fetchMyPostList()
        emojiViewModel.fetchMyCreatedEmojiList()
        emojiViewModel.fetchMySavedEmojiList()
    }

    Column(Modifier.background(White).fillMaxSize()) {
        TopNavigationBar("Profile", shouldNavigate = false)

        if (currentUser?.name.isNullOrEmpty()) {
            EmptyProfile()
        } else {
            LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Username",
                            fontSize = 12.sp,
                            color = EmojiHubDetailLabel
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "@" + currentUser?.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(34.dp))
                    }

                    Divider(color = Color.EmojiHubDividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileMenuCellWithPreview(
                        label = "내가 작성한 포스트",
                        detailLabel = myPostList.itemCount.toString(),
                        navigateToDestination = { navController.navigate(NavigationDestination.MyPostList) }
                    ) {
                        items(myPostList.itemCount) { index ->
                            myPostList[index]?.let {
                                PreviewPostCell(post = it)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = Color.EmojiHubDividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileMenuCellWithPreview(
                        label = "내가 만든 이모지",
                        detailLabel = "더보기",
                        navigateToDestination = { navController.navigate(NavigationDestination.MyEmojiList) }
                    ) {
                        items(myCreatedEmojiList.itemCount) { index ->
                            myCreatedEmojiList[index]?.let { emoji ->
                                EmojiCell(
                                    emoji = emoji,
                                    displayMode = EmojiCellDisplay.HORIZONTAL,
                                    onSelected = {
                                        emojiViewModel.currentEmoji = emoji
                                        navController.navigate(NavigationDestination.PlayEmojiVideo)
                                    })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ProfileMenuCellWithPreview(
                        label = "저장된 이모지",
                        detailLabel = "더보기",
                        navigateToDestination = { navController.navigate(NavigationDestination.MySavedEmojiList) }
                    ) {
                        items(mySavedEmojiList.itemCount) { index ->
                            mySavedEmojiList[index]?.let { emoji ->
                                EmojiCell(
                                    emoji = emoji,
                                    displayMode = EmojiCellDisplay.HORIZONTAL,
                                    onSelected = {
                                        emojiViewModel.currentEmoji = emoji
                                        navController.navigate(NavigationDestination.PlayEmojiVideo)
                                    })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Divider(color = Color.EmojiHubDividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileMenuCell(label = "로그아웃") {
                        showLogoutDialog = true
                    }
                    ProfileMenuCell(label = "회원 탈퇴", isDestructive = true) {
                        showSignOutDialog = true
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (showLogoutDialog) {
                CustomDialog(
                    title = "로그아웃",
                    body = "로그아웃하시겠습니까?",
                    needsCancelButton = true,
                    onDismissRequest = { showLogoutDialog = false },
                    dismiss = { showLogoutDialog = false },
                    confirm = {
                        navController.navigateAsOrigin(NavigationDestination.Onboard)
                        userViewModel.logout()
                    }
                )
            }

            if (showSignOutDialog) {
                CustomDialog(
                    title = "회원 탈퇴",
                    body = "계정을 삭제하시겠습니까?",
                    confirmText = "삭제",
                    isDestructive = true,
                    needsCancelButton = true,
                    onDismissRequest = { showSignOutDialog = false },
                    dismiss = { showSignOutDialog = false },
                    confirm = {
                        navController.navigateAsOrigin(NavigationDestination.Onboard)
                        userViewModel.signOut()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage()
}