package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.EmojiViewModel

@Composable
fun CreatedEmojiListView(
    emojiViewModel: EmojiViewModel
) {
    val navController = LocalNavController.current

    val emojiList = emojiViewModel.myCreatedEmojiList.collectAsLazyPagingItems()

    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    Column (
        Modifier.background(Color.White)
    ) {
        TopNavigationBar(
            title = "내가 만든 이모지",
            navigate = { navController.popBackStack() }
        )

        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Column {
                    Button(
                        onClick = { dropDownMenuExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Sort by", fontSize = 12.sp)
                    }

                    DropdownMenu(
                        expanded = dropDownMenuExpanded,
                        onDismissRequest = { dropDownMenuExpanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            emojiViewModel.sortByDate = 1
                            emojiViewModel.fetchEmojiList()
                            dropDownMenuExpanded = false
                        }) {
                            Text(text = "created date")
                        }
                        DropdownMenuItem(onClick = {
                            emojiViewModel.sortByDate = 0
                            emojiViewModel.fetchEmojiList()
                            dropDownMenuExpanded = false
                        }) {
                            Text(text = "save count")
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(emojiList.itemCount) { index ->
                    emojiList[index]?.let{
                        EmojiCell(emoji = it, displayMode = EmojiCellDisplay.VERTICAL) { selectedEmoji ->
                            emojiViewModel.currentEmoji = selectedEmoji
                            navController.navigate(NavigationDestination.PlayEmojiVideo)
                        }
                    }
                }
            }
        }
    }
}