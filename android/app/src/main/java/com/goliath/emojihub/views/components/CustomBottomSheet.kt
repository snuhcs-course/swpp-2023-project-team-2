package com.goliath.emojihub.views.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.ui.theme.Color.EmojiHubDividerColor
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.ui.theme.Color.LightGray
import kotlinx.coroutines.launch
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.PostViewModel
import com.goliath.emojihub.viewmodels.ReactionViewModel

enum class BottomSheetContent {
    VIEW_REACTION, ADD_REACTION, EMPTY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet (
    bottomSheetContent: BottomSheetContent,
    emojiList: List<Emoji>,
    emojiCellClicked: (Emoji) -> Unit
){
    val bottomSheetState = LocalBottomSheetController.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel = hiltViewModel<EmojiViewModel>()
    val reactionViewModel = hiltViewModel<ReactionViewModel>()
    val postViewModel = hiltViewModel<PostViewModel>()
    val navController = LocalNavController.current

    var selectedEmojiClass by remember { mutableStateOf<String?>("전체") }
    val emojisByClass = emojiList.groupBy { it.unicode }
    val emojiClassFilters = listOf("전체") + emojisByClass.keys.toList()
    val emojiCounts = emojisByClass.mapValues { it.value.size }

    val myCreatedEmojiList = viewModel.myCreatedEmojiList.collectAsLazyPagingItems()
    val mySavedEmojiList = viewModel.mySavedEmojiList.collectAsLazyPagingItems()
    var displayMyCreatedEmojis by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchMyCreatedEmojiList()
        viewModel.fetchMySavedEmojiList()
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
                            emojiClass = emojiClassFilters,
                            emojiCounts = emojiCounts,
                            onEmojiClassSelected = { selectedEmojiClass = it}
                        ) {
                            items(emojiClassFilters.size) { emojiClass ->
                                EmojiClassFilterButton(
                                    text = if (emojiClassFilters[emojiClass] == "전체") "전체" else "${emojiClassFilters[emojiClass].toEmoji()}${emojiCounts[emojiClassFilters[emojiClass]]}",
                                    isSelected = emojiClassFilters[emojiClass] == selectedEmojiClass,
                                    onSelected = {
                                        selectedEmojiClass = emojiClassFilters[emojiClass]
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                displayMyCreatedEmojis = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (displayMyCreatedEmojis) LightGray else White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "내가 만든 이모지",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                displayMyCreatedEmojis = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (!displayMyCreatedEmojis) LightGray else White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "저장된 이모지",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Divider(color = EmojiHubDividerColor, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

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
                        items(if (selectedEmojiClass == "전체") emojiList else emojiList.filter { it.unicode == selectedEmojiClass }, key = { it.id }) { emoji ->
                            EmojiCell(emoji = emoji, displayMode = EmojiCellDisplay.VERTICAL) {selectedEmoji ->
                                viewModel.currentEmoji = selectedEmoji
                                navController.navigate(NavigationDestination.PlayEmojiVideo) //FIXME: make a new destination or fix PlayEmojiVideo's back stack to include BottomSheet
                            }
                        }
                    }

                    BottomSheetContent.ADD_REACTION -> {
                        if (displayMyCreatedEmojis) {
                            items(myCreatedEmojiList.itemCount) { index ->
                                myCreatedEmojiList[index]?.let {
                                    EmojiCell(emoji = it, displayMode = EmojiCellDisplay.VERTICAL) {
                                        //TODO: add reaction to post
                                        coroutineScope.launch {
                                            val success = reactionViewModel.uploadReaction(postId = postViewModel.currentPostId, emojiId = it.id)
                                            if (success) {
                                                Log.d("addReaction", "postId = ${postViewModel.currentPostId}, emojiId = ${it.id}")
                                                showSuccessDialog = true
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            items(mySavedEmojiList.itemCount) { index ->
                                mySavedEmojiList[index]?.let {
                                    EmojiCell(emoji = it, displayMode = EmojiCellDisplay.VERTICAL) {
                                        //TODO: add emoji reaction to post
                                        coroutineScope.launch {
                                            val success = reactionViewModel.uploadReaction(postId = postViewModel.currentPostId, emojiId = it.id)
                                            if (success) {
                                                Log.d("addReaction", "postId = ${postViewModel.currentPostId}, emojiId = ${it.id}")
                                                showSuccessDialog = true
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
        if (showSuccessDialog) {
            CustomDialog(
                title = "완료",
                body = "반응이 추가 되었습니다.",
                confirm = { navController.popBackStack() }
            )
        }
    }
}