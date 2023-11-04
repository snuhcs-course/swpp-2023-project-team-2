package com.goliath.emojihub.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.Black
import com.goliath.emojihub.ui.theme.Color.EmojiHubDetailLabel
import com.goliath.emojihub.ui.theme.Color.EmojiHubLabel
import com.goliath.emojihub.ui.theme.Color.EmojiHubRed
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.components.EmptyProfile
import com.goliath.emojihub.views.components.ProfileMenuCell
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun ProfilePage(

) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val currentUser = userViewModel.userState.collectAsState().value

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    Column(Modifier.background(White)) {
        TopNavigationBar("Profile", shouldNavigate = false)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (!currentUser?.accessToken.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
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
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.EmojiHubDividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileMenuCell(label = "내가 작성한 포스트", needsTrailingButton = true) {}
                    ProfileMenuCell(label = "내가 만든 이모지", needsTrailingButton = true) {}
                    ProfileMenuCell(label = "저장된 이모지", needsTrailingButton = true) {}

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.EmojiHubDividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileMenuCell(label = "로그아웃") {
                        showLogoutDialog = true
                    }
                    ProfileMenuCell(label = "회원 탈퇴", isDestructive = true) {
                        showSignOutDialog = true
                    }
                }

                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text(text = "로그아웃", fontWeight = FontWeight.Bold) },
                        text = { Text(text = "로그아웃하시겠습니까?") },
                        shape = RoundedCornerShape(20.dp),
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text(text = "취소", color = EmojiHubLabel)
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { userViewModel.logout() }) {
                                Text(text = "확인", color = Black)
                            }
                        }
                    )
                }

                if (showSignOutDialog) {
                    AlertDialog(
                        onDismissRequest = { showSignOutDialog = false },
                        title = { Text(text = "회원 탈퇴", fontWeight = FontWeight.Bold) },
                        text = { Text(text = "계정을 삭제하시겠습니까?") },
                        shape = RoundedCornerShape(20.dp),
                        dismissButton = {
                            TextButton(onClick = { showSignOutDialog = false }) {
                                Text(text = "취소", color = EmojiHubLabel)
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { userViewModel.signOut() }) {
                                Text(text = "삭제", color = EmojiHubRed)
                            }
                        }
                    )
                }
            } else {
                EmptyProfile()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage()
}