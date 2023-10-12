package com.goliath.emojihub.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun PreviewLoginView() {
    LoginPage(onLoginClicked = { _, _ -> },
                onCreateAccountClicked = {})
}

@Composable
fun LoginPage(
    onLoginClicked: (String, String) -> Unit,
    onCreateAccountClicked: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Your UI components here
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        Text("EmojiHub")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onLoginClicked(username, password) }
        ) {
            Text("로그인")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onCreateAccountClicked() }
        ) {
            Text("계정 생성")
        }
    }
}
