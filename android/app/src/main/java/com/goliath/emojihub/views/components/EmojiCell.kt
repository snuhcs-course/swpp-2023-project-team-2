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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.ui.theme.Color.Black
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel

@Composable
fun EmojiCell (
    emoji: Emoji,
    onSelected: (Emoji) -> Unit
) {
    val width_dp = 292.dp // Width of the Card
    val height_dp = LocalConfiguration.current.screenWidthDp.dp

    val width = with(LocalDensity.current) { width_dp.toPx().toInt()}
    val height = with(LocalDensity.current) { height_dp.toPx().toInt()}

    val viewModel = hiltViewModel<EmojiViewModel>()

//    LaunchedEffect(emoji.videoLink) {
//        viewModel.createVideoThumbnail(emoji.videoLink, width, height)
//    }

    val thumbnailBitmap = viewModel.thumbnailState.collectAsState().value

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(292.dp)
            .clickable { onSelected(emoji) },
        shape = RoundedCornerShape(4.dp),
        elevation = 0.dp
    ) {
//        thumbnailBitmap?.let { bitmap ->
//            Image(
//                bitmap = bitmap.asImageBitmap(),
//                contentDescription = "Video Thumbnail",
//                modifier = Modifier.fillMaxSize()
//            )
//        } ?:
        Box(Modifier.fillMaxSize().background(Black).alpha(0.25F))

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