package com.goliath.emojihub.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.extensions.toEmoji
import com.goliath.emojihub.models.Emoji
import kotlinx.coroutines.launch
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel

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

    var selectedEmojiClass = viewModel.selectedEmojiClass
    val emojisByClass = emojiList.groupBy { it.unicode }
    val allCategory = listOf("전체") + emojisByClass.keys.toList()
    val emojiCounts = emojisByClass.mapValues { it.value.size }

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        },
        containerColor = White,
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
                        //TODO: display each emoji class (as an IconButton?) and its count [Figma]
                        EmojiClassFilterRow(
                            emojiClass = allCategory,
                            emojiCounts = emojiCounts,
                            onEmojiClassSelected = { selectedEmojiClass = it}
                        ) {
                            items(allCategory.size) { emojiClass ->
                                EmojiClassFilterButton(
                                    text = if (allCategory[emojiClass] == "전체") "전체" else "${allCategory[emojiClass].toEmoji()}${emojiCounts[allCategory[emojiClass]]}",
                                    isSelected = allCategory[emojiClass] == selectedEmojiClass,
                                    onSelected = { selectedEmojiClass = allCategory[emojiClass] }
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
                                // TODO: fetch user's emojis and display
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
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
                                // TODO: fetch user's saved emojis and display
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
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

        Spacer(modifier = Modifier.weight(1f))

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
                        emojisByClass[selectedEmojiClass]?.let { emojilist ->
                            items(emojilist, key = { it.id }) { emoji ->
                                EmojiCell(emoji = emoji) {
                                    emojiCellClicked(emoji)
                                }
                            }
                        }
                    }

                    BottomSheetContent.ADD_REACTION -> {
                        items(emojiList, key = { it.id }) { emoji ->
                            EmojiCell(emoji = emoji) {
                                emojiCellClicked(emoji)
                            }
                        }
                    }
                }
            }
        }
    }
}