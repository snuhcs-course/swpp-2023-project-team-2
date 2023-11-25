package com.goliath.emojihub.views.components

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.BottomSheetController
import com.goliath.emojihub.data_sources.BottomSheetControllerImpl
import com.goliath.emojihub.extensions.reactionsToString
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.dummyPost
import com.goliath.emojihub.ui.theme.Color.EmojiHubDetailLabel
import com.goliath.emojihub.viewmodels.EmojiViewModel

@Composable
fun PostCell(
    post: Post,
    bottomSheetController: BottomSheetController = remember { BottomSheetControllerImpl() }
) {
    val viewModel = hiltViewModel<EmojiViewModel>()
    val state by bottomSheetController.bottomSheetState.collectAsState()

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
                TextButton(
                    onClick = {
                        val emojistr = reactionsToString(post.reaction)
                        /*TODO*/
                        Log.d("emojiStr", emojistr)
                    },

                ) {
                    Text(
                        text = reactionsToString(post.reaction),
                        fontSize = 13.sp,
                        color = EmojiHubDetailLabel
                    )
                }

                IconButton(onClick = {
//                    viewModel.isBottomSheetShown = true
                    Log.d("FeedPage", "bottomSheetState0: $state")
                    bottomSheetController.setBottomSheetState(true)
                    Log.d("FeedPage", "bottomSheetState1: $state")
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

//@Preview(showBackground = true)
//@Composable
//fun PostCellPreview() {
//    PostCell(dummyPost)
//}