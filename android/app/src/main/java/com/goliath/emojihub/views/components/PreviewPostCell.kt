package com.goliath.emojihub.views.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.ui.theme.Color.EmojiHubBorderColor
import com.goliath.emojihub.ui.theme.Color.EmojiHubDetailLabel

@Composable
fun PreviewPostCell(
    post: Post
) {
    Column(
        Modifier.width(240.dp)
            .border(width = 1.dp, color = EmojiHubBorderColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "@" + post.createdBy,
                fontSize = 14.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = post.createdAt,
                fontSize = 12.sp,
                color = EmojiHubDetailLabel
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = post.content,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.height(64.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = post.reaction.count().toString() + "개의 반응",
            fontSize = 13.sp,
            color = EmojiHubDetailLabel
        )
    }
}