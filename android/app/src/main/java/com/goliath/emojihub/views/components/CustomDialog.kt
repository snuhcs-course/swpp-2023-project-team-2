package com.goliath.emojihub.views.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.ui.theme.Color

@Composable
fun CustomDialog(
    title: String,
    body: String = "",
    confirmText: String = "확인",
    isDestructive: Boolean = false,
    needsCancelButton: Boolean = false,
    onDismissRequest: () -> Unit = {},
    confirm: () -> Unit = {},
    dismiss: () -> Unit = {}
) {
    if (needsCancelButton) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Text(body) },
            shape = RoundedCornerShape(20.dp),
            dismissButton = {
                TextButton(onClick = { dismiss() }) {
                    Text(text = "취소", color = Color.EmojiHubLabel)
                }
            },
            confirmButton = {
                TextButton(onClick = { confirm() }) {
                    Text(text = confirmText, color =
                    if (isDestructive) Color.EmojiHubRed
                    else Color.Black
                    )
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Text(body) },
            shape = RoundedCornerShape(20.dp),
            confirmButton = {
                TextButton(onClick = { confirm() }) {
                    Text(text = confirmText, color =
                    if (isDestructive) Color.EmojiHubRed
                    else Color.Black
                    )
                }
            }
        )
    }
}