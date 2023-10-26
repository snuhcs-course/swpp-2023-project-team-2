package com.goliath.emojihub.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.models.dummyPost
import com.goliath.emojihub.ui.theme.Color.EmojiHubDividerColor
import com.goliath.emojihub.views.components.PostCell

@Composable
fun FeedPage(
    postList: List<Post>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(postList) { post ->
            PostCell(post = post)
            Divider(color = EmojiHubDividerColor, thickness = 0.5.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedPagePreview() {
    FeedPage(postList = (1..10).map { dummyPost })
}