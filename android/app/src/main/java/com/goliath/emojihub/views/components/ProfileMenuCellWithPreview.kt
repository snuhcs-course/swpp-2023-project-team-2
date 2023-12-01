package com.goliath.emojihub.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.ui.theme.Color

@Composable
fun ProfileMenuCellWithPreview(
    label: String,
    detailLabel: String,
    navigateToDestination: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = detailLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.EmojiHubGrayIcon
            )
            IconButton(onClick = { navigateToDestination() }) {
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = null,
                    modifier = Modifier.size(size = 24.dp),
                    tint = Color.EmojiHubGrayIcon
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            content()
        }
    }
}