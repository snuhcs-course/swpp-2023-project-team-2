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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.models.createDummyEmoji
import com.goliath.emojihub.viewmodels.EmojiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet (
    onDismissRequest: () -> Unit = {},
    isViewReaction: Boolean = false,
    isAddReaction: Boolean = false,
){
    val emojiViewModel = hiltViewModel<EmojiViewModel>()
    val navController = LocalNavController.current
    val emojiList = (1..10).map { createDummyEmoji() }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },

    ) {
        if (isViewReaction) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //TODO: display each emoji class (as an IconButton?) and its count [Figma]
                    Text(
                        text = "전체",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        else if (isAddReaction) {
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
                            .padding(horizontal = 15.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        )
                    )
                    {
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
                            .padding(horizontal = 15.dp),
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

        Spacer(modifier = Modifier.weight(1f))

        Column(Modifier.padding(horizontal = 16.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(emojiList, key = { it.id }) { emoji ->
                    EmojiCell(emoji = emoji) {
                        emojiViewModel.currentEmoji = emoji
                        navController.navigate(NavigationDestination.PlayEmojiVideo)
                    }
                }
            }
        }
    }
}