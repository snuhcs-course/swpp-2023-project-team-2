package com.goliath.emojihub.views.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import com.goliath.emojihub.models.Emoji
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.ui.theme.Color.Black
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel

@Composable
fun EmojiCell (
    emoji: Emoji,
    onSelected: (Emoji) -> Unit
) {
    //replace to emoji.thumbnailLink
    val thumbnailLink = emoji.thumbnailLink.takeIf{ it.isNotEmpty()} ?:"https://i.pinimg.com/236x/4b/05/0c/4b050ca4fcf588eedc58aa6135f5eecf.jpg"
    Log.d("create_TN", "${emoji.thumbnailLink}")
    val context = LocalContext.current

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(292.dp)
            .clickable { onSelected(emoji) },
        shape = RoundedCornerShape(4.dp),
        elevation = 0.dp
    ) {
        Box(Modifier.fillMaxSize().background(Color.Gray).alpha(0.25F))
        Image(
            painter = rememberAsyncImagePainter(thumbnailLink),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "@" + emoji.createdBy,
                modifier = Modifier.align(Alignment.TopStart),
                fontSize = 12.sp,
                color = White
            )

            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.size(16.dp),
                    onClick = {
                        // TODO: save emoji
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.SaveAlt,
                        contentDescription = "",
                        tint = White
                    )
                }

                Text(
                    text = emoji.savedCount.toString(),
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 12.sp,
                    color = White
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
}