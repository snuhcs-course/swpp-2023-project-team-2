package com.goliath.emojihub.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.models.createDummyEmoji
import com.goliath.emojihub.views.components.EmojiCell

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddReactionBottomSheet(
) {
    val navController = LocalNavController.current
    val sheetState = rememberModalBottomSheetState()
    var isSheetShown by rememberSaveable { mutableStateOf(true) }
    val emojiList = (1..10).map { createDummyEmoji() }

    if(isSheetShown) {
        ModalBottomSheet(
            onDismissRequest = {
                isSheetShown = false
                navController.popBackStack()
            },
            sheetState = sheetState,) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                                  // TODO: fetch user's emojis and display
                        },
                        modifier = Modifier
                            .padding(horizontal = 15.dp),
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
                    Button(
                        onClick = {
                                  // TODO: fetch user's saved emojis and display
                        },
                        modifier = Modifier
                            .padding(horizontal = 15.dp),
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

            Spacer(modifier = Modifier.weight(1f))

            Column(Modifier.padding(horizontal = 16.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                    items(emojiList.size) {index ->
                        EmojiCell(emoji = emojiList[index])
                    }
                }
            }
        }
    }
}

