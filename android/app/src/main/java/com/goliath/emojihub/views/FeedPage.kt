package com.goliath.emojihub.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.createDummyEmoji
import com.goliath.emojihub.models.dummyPost
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.EmojiHubDividerColor
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.views.components.EmojiCell
import com.goliath.emojihub.views.components.PostCell
import com.goliath.emojihub.views.components.TopNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    postList: List<Post>
) {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<EmojiViewModel>()
    val emojiList = (1..10).map { createDummyEmoji() }

    Column (
        Modifier.background(Color.White)
    ) {
        TopNavigationBar("Feed", shouldNavigate = false) {
            IconButton(onClick = {
                navController.navigate(NavigationDestination.CreatePost)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = ""
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(postList) { post ->
                    PostCell(post = post)
                    Divider(color = EmojiHubDividerColor, thickness = 0.5.dp)
                }
            }
        }
    }

    if (viewModel.isBottomSheetShown) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.isBottomSheetShown = false
                navController.popBackStack()
            }
        ) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            // TODO: fetch user's emojis and display
                        },
                        modifier = Modifier
                            .padding(horizontal = 15.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = androidx.compose.ui.graphics.Color.White,
                            contentColor = androidx.compose.ui.graphics.Color.Black
                        )
                    )
                    {
                        Text(
                            text = "내가 만든 이모지",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            // TODO: fetch user's saved emojis and display
                        },
                        modifier = Modifier
                            .padding(horizontal = 15.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = androidx.compose.ui.graphics.Color.White,
                            contentColor = androidx.compose.ui.graphics.Color.Black
                        )
                    ) {
                        Text(
                            text = "저장된 이모지",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(Modifier.padding(horizontal = 16.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(emojiList.size) {index ->
                        EmojiCell(emoji = emojiList[index])
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedPagePreview() {
    FeedPage(postList = (1..10).map { dummyPost })
}