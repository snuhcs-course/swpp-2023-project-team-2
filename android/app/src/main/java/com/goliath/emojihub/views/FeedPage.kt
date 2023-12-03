package com.goliath.emojihub.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.models.createDummyEmoji
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.EmojiHubDividerColor
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.PostViewModel
import com.goliath.emojihub.views.components.EmojiCell
import com.goliath.emojihub.views.components.EmojiCellDisplay
import com.goliath.emojihub.views.components.CustomBottomSheet
import com.goliath.emojihub.views.components.PostCell
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun FeedPage() {
    val navController = LocalNavController.current
    val bottomSheetController = LocalBottomSheetController.current

    val emojiViewModel = hiltViewModel<EmojiViewModel>()
    val postViewModel = hiltViewModel<PostViewModel>()

    val emojiList = (1..10).map { createDummyEmoji() }
    val postList = postViewModel.postList.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        postViewModel.fetchPostList()
    }

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
                items(postList.itemCount) { index ->
                    postList[index]?.let {
                        PostCell(post = it)
                        Divider(color = EmojiHubDividerColor, thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    if (bottomSheetController.isVisible) {
        CustomBottomSheet(
            bottomSheetContent = emojiViewModel.bottomSheetContent,
            emojiList = emojiList
        ) { emoji ->
            emojiViewModel.currentEmoji = emoji
            navController.navigate(NavigationDestination.PlayEmojiVideo)
        }
    }
}