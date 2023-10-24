package com.goliath.emojihub.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.EmojiHubDetailLabel
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.components.ProfileMenuCell

@Composable
fun ProfilePage(

) {
    //val userViewModel = hiltViewModel<UserViewModel>()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // TODO: add NavigationBar
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
                    text = "@example",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.EmojiHubDividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuCell(label = "내가 작성한 포스트", needsTrailingButton = true) {
                // for test
                Log.d("profile menu", "내가 작성한 포스트")
            }
            ProfileMenuCell(label = "내가 만든 이모지", needsTrailingButton = true) {}
            ProfileMenuCell(label = "저장된 이모지", needsTrailingButton = true) {}

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.EmojiHubDividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuCell(label = "로그아웃") {}
            ProfileMenuCell(label = "회원 탈퇴", isDestructive = true) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage()
}