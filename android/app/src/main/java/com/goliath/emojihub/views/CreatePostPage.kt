package com.goliath.emojihub.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.PostViewModel
import com.goliath.emojihub.views.components.TopNavigationBar
import kotlinx.coroutines.launch

@Composable
fun CreatePostPage(
    viewModel: PostViewModel
) {
    val navController = LocalNavController.current

    val coroutineScope = rememberCoroutineScope()

    var content by remember { mutableStateOf(TextFieldValue("")) }

    Column {
        TopNavigationBar(
            navigate = { navController.popBackStack() }
        ) {
            TextButton(onClick = {
                coroutineScope.launch {
                    viewModel.uploadPost(content.text)
                }
            }) {
                Text(text = "작성", color = Color.Black)
            }
        }

        TextField(
            modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("오늘 무슨 일이 있었나요?") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                placeholderColor = Color.LightGray,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}