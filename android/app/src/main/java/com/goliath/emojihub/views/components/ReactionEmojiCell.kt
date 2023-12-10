package com.goliath.emojihub.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.viewmodels.UserViewModel


@Composable
fun ReactionEmojiCell (
    emoji: Emoji,
    reactedBy: String,
    onSelected: (Emoji) -> Unit
) {
    val thumbnailLink = emoji.thumbnailLink.takeIf{ it.isNotEmpty()} ?:"https://i.pinimg.com/236x/4b/05/0c/4b050ca4fcf588eedc58aa6135f5eecf.jpg"

    val userViewModel = hiltViewModel<UserViewModel>()
    val currentUser = userViewModel.userState.collectAsState().value

    val textModifier = if (currentUser?.name == reactedBy) {
        Modifier
            .background(color = com.goliath.emojihub.ui.theme.Color.EmojiHubYellow, shape = RoundedCornerShape(10.dp))
            .padding(4.dp)
    } else {
        Modifier
    }

    Column {
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .height(292.dp)
                .clickable { onSelected(emoji) },
            shape = RoundedCornerShape(4.dp),
            elevation = 0.dp
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
                    .alpha(0.25F))

            Image(
                painter = rememberAsyncImagePainter(thumbnailLink),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Box(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "@" + emoji.createdBy,
                    modifier = Modifier.align(Alignment.TopStart),
                    fontSize = 12.sp,
                    color = com.goliath.emojihub.ui.theme.Color.White
                )

                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(16.dp),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SaveAlt,
                            contentDescription = "",
                            tint = com.goliath.emojihub.ui.theme.Color.White
                        )
                    }

                    Text(
                        text = emoji.savedCount.toString(),
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 12.sp,
                        color = com.goliath.emojihub.ui.theme.Color.White
                    )
                }
            }

            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = emoji.unicode.toEmoji(),
                    fontSize = 44.sp
                )
            }
        }
        if (currentUser != null) {
            Text(
                text = "@$reactedBy",
                modifier = textModifier.align(Alignment.Start),
                fontSize = 12.sp,
                color = com.goliath.emojihub.ui.theme.Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}