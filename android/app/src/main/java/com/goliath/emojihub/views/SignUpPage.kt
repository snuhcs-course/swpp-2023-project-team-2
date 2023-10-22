package com.goliath.emojihub.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun SignUpPagePreview() {
    SignUpPage()
}


@Composable
fun SignUpPage() {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    // For "Already a member? Log in"
    var isClicked by remember { mutableStateOf(false) }
    val textColor by animateColorAsState(if (isClicked) Color.LightGray else Color.DarkGray)


    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
                focusManager.clearFocus()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_horizontal),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 48.dp)
            )
            TextField(
                value = username,
                onValueChange = {
                    username = it
                },
                placeholder = {
                    Text(
                        text = "Username",
                        color = Color.LightGray
                    )
                },
                modifier = Modifier
                    .onFocusChanged { it.isFocused }
                    .fillMaxWidth()
                    .height(50.dp),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.LightGray,
                ),
                singleLine = true
            )
            TextField(
                value = email,
                onValueChange = {
                    email = it
                },
                placeholder = {
                    Text(
                        text = "Email",
                        color = Color.LightGray
                    )
                },
                modifier = Modifier
                    .onFocusChanged { it.isFocused }
                    .fillMaxWidth(),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
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
                placeholder = {
                    Text(
                        text = "Password",
                        color = Color.LightGray
                    )
                },
            modifier = Modifier
                .onFocusChanged { it.isFocused }
                .fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.LightGray,
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
            )
            Text(
                    text = "Already a member? Log In",
            color = textColor,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier
                .clickable(onClick = {
                    isClicked = !isClicked
                    /* TODO Handle 비회원 모드 Click*/
                })
                .padding(8.dp)
            )
            OutlinedButton(
                onClick = { /* TODO Handle Sign Up Click */ },
                modifier = Modifier
                    .offset(y = (10).dp)
                    .fillMaxWidth()
                    .height(50.dp),
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
        }
    }
}
