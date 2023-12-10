package com.goliath.emojihub.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.ui.theme.Color.EmojiHubDividerColor
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.PostViewModel
import com.goliath.emojihub.viewmodels.ReactionViewModel
import kotlinx.coroutines.launch

enum class BottomSheetContent {
    VIEW_REACTION, ADD_REACTION, EMPTY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet (
    bottomSheetContent: BottomSheetContent
){
    val bottomSheetState = LocalBottomSheetController.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel = hiltViewModel<EmojiViewModel>()

    val reactionViewModel = hiltViewModel<ReactionViewModel>()
    val postViewModel = hiltViewModel<PostViewModel>()
    val navController = LocalNavController.current

    val myCreatedEmojiList = viewModel.myCreatedEmojiList.collectAsLazyPagingItems()
    val mySavedEmojiList = viewModel.mySavedEmojiList.collectAsLazyPagingItems()
    val reactionList = reactionViewModel.reactionList.collectAsLazyPagingItems()
    var displayMyCreatedEmojis by remember { mutableStateOf(true) }

    var selectedEmojiClass by remember { mutableStateOf<String?>("전체") }
    val emojisByUnicode = postViewModel.currentPost.reaction.groupBy { it.emoji_unicode }
    val emojiUnicodeFilters = listOf("전체") + emojisByUnicode.keys.toList()
    val emojiCounts = emojisByUnicode.mapValues { it.value.size }
    var selectedEmojiUnicode by remember { mutableStateOf("") }

    LaunchedEffect(selectedEmojiUnicode) {
        reactionViewModel.fetchReactionList(postViewModel.currentPostId, selectedEmojiUnicode)
    }

    LaunchedEffect(bottomSheetContent) {
        if(bottomSheetContent == BottomSheetContent.ADD_REACTION) {
            viewModel.fetchMyCreatedEmojiList()
            viewModel.fetchMySavedEmojiList()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        },
        containerColor = White,
        modifier = Modifier.fillMaxHeight(),
    ) {
        when (bottomSheetContent) {
            BottomSheetContent.EMPTY -> {}
            BottomSheetContent.VIEW_REACTION -> {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EmojiClassFilterRow(
                            emojiClass = emojiUnicodeFilters,
                            emojiCounts = emojiCounts,
                            onEmojiClassSelected = { selectedEmojiClass = it }
                        ) {
                            items(emojiUnicodeFilters.size) { unicode ->
                                EmojiClassFilterButton(
                                    text = if (emojiUnicodeFilters[unicode] == "전체") "전체" else "${emojiUnicodeFilters[unicode].toEmoji()}${emojiCounts[emojiUnicodeFilters[unicode]]}",
                                    isSelected = emojiUnicodeFilters[unicode] == selectedEmojiClass,
                                    onSelected = {
                                        selectedEmojiClass = emojiUnicodeFilters[unicode]
                                        selectedEmojiUnicode = if (emojiUnicodeFilters[unicode] == "전체") "" else emojiUnicodeFilters[unicode]
                                    }
                                )
                            }
                        }
                    }
                }
            }

            BottomSheetContent.ADD_REACTION -> {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EmojiClassFilterButton(
                            text = "내가 만든 이모지",
                            isSelected = displayMyCreatedEmojis
                        ) {
                            displayMyCreatedEmojis = true
                        }

                        EmojiClassFilterButton(
                            text = "저장된 이모지",
                            isSelected = !displayMyCreatedEmojis
                        ) {
                            displayMyCreatedEmojis = false
                        }
                    }
                }
            }
        }
        Divider(color = EmojiHubDividerColor, thickness = 0.5.dp)
        Column(Modifier.padding(horizontal = 16.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                when (bottomSheetContent) {
                    BottomSheetContent.EMPTY -> {}
                    BottomSheetContent.VIEW_REACTION -> {
                        items(reactionList.itemCount) { index ->
                            reactionList[index]?.let {
                                val emojiDto = it.emojiDto
                                val reactedBy = it.createdBy
                                if (emojiDto != null){
                                    ReactionEmojiCell(emoji = Emoji(emojiDto), reactedBy = reactedBy) { selectedEmoji ->
                                        viewModel.currentEmoji = selectedEmoji
                                        navController.navigate(NavigationDestination.PlayEmojiVideo)
                                    }
                                }
                            }
                        }
                    }

                    BottomSheetContent.ADD_REACTION -> {
                        if (displayMyCreatedEmojis) {
                            items(myCreatedEmojiList.itemCount) { index ->
                                myCreatedEmojiList[index]?.let {
                                    EmojiCell(emoji = it, displayMode = EmojiCellDisplay.VERTICAL) {
                                        coroutineScope.launch {
                                            val success = reactionViewModel.uploadReaction(postId = postViewModel.currentPostId, emojiId = it.id)
                                            if (success) {
                                                coroutineScope.launch {
                                                    bottomSheetState.hide()
                                                }
                                                postViewModel.fetchPostList()
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            items(mySavedEmojiList.itemCount) { index ->
                                mySavedEmojiList[index]?.let {
                                    EmojiCell(emoji = it, displayMode = EmojiCellDisplay.VERTICAL) {
                                        coroutineScope.launch {
                                            val success = reactionViewModel.uploadReaction(postId = postViewModel.currentPostId, emojiId = it.id)
                                            if (success) {
                                                coroutineScope.launch {
                                                    bottomSheetState.hide()
                                                }
                                                postViewModel.fetchPostList()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}