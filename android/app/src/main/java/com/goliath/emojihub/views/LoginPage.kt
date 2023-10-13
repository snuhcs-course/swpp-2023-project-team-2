package com.goliath.emojihub.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.R
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.UserViewModel

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    LoginPage()
}


@Composable
fun LoginPage() {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var isUsernameFocused by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.emojihub),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
                    .offset(y = (-40).dp),
                contentScale = ContentScale.Fit
            )
            TextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = {
                    if (username.text.isEmpty() && !isUsernameFocused) {
                        Text(
                            text = "Username",
                            color = Color.LightGray
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        color = Color.White,
                    )
                    .padding(bottom = 1.dp)
                    .onFocusChanged { focusState ->
                        isUsernameFocused = focusState.isFocused
                    }
                    .offset(y = (-40).dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White, // Set the background color to white
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.LightGray,
                   ),
                singleLine = true
            )
            TextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    if (password.text.isEmpty() && !isPasswordFocused) {
                        Text(
                            text = "Password",
                            color = Color.LightGray
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        color = Color.White,
                    )
                    .padding(bottom = 1.dp)
                    .onFocusChanged { focusState ->
                        isPasswordFocused = focusState.isFocused
                    }
                    .offset(y = (-40).dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White, // Set the background color to white
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.LightGray,
                ),
                singleLine = true
            )
            Button(
                onClick = { /* TODO Handle Login Click*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ), content = {
                    Text(
                        text = "로그인",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            OutlinedButton(
                onClick = { /* TODO Handle Sign Up Click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                content = {
                    Text(
                        text = "계정 생성",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Text(
                text = "비회원 모드로 시작하기",
                color = Color.DarkGray, // Adjust color as required
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .clickable(onClick = { })
                    .padding(8.dp)
                    .offset (y = 40.dp )
            )
        }
    }
}
