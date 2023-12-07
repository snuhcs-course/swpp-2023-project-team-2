package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.EmojiViewModel

@Composable
fun SavedEmojiListView(
    emojiViewModel: EmojiViewModel
) {
    val navController = LocalNavController.current

    val emojiList = emojiViewModel.mySavedEmojiList.collectAsLazyPagingItems()

    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    Column (
        Modifier.background(Color.White)
    ) {
        TopNavigationBar(
            title = "저장된 이모지",
            navigate = { navController.popBackStack() }
        )

        Column(Modifier.padding(horizontal = 16.dp)) {
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