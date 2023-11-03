package com.goliath.emojihub.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.dummyPost
import com.goliath.emojihub.ui.theme.Color.EmojiHubDividerColor
import com.goliath.emojihub.views.components.PostCell
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun FeedPage(
    postList: List<Post>
) {
    Column (

    ) {
        TopNavigationBar("Feed", shouldNavigate = false) {
            IconButton(onClick = {
                print("something")
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
}

@Preview(showBackground = true)
@Composable
fun FeedPagePreview() {
    FeedPage(postList = (1..10).map { dummyPost })
}