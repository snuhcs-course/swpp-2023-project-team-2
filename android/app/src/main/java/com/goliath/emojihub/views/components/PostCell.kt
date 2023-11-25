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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.ui.theme.Color.EmojiHubDetailLabel
import kotlinx.coroutines.launch

@Composable
fun PostCell(
    post: Post
) {
    val bottomSheetState = LocalBottomSheetController.current
    val coroutineScope = rememberCoroutineScope()

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

            // TODO: should be replaced according to Figma
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.reaction.toString(),
                    fontSize = 13.sp,
                    color = EmojiHubDetailLabel
                )
                IconButton(onClick = {
                    coroutineScope.launch { bottomSheetState.show() }
                }) {
                    Icon(
                        imageVector = Icons.Filled.AddReaction,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}