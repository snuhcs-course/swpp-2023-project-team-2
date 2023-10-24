package com.goliath.emojihub.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.views.components.UnderlinedTextField

@Composable
fun SignUpPage() {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
                focusManager.clearFocus()
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UnderlinedTextField(
                    content = email,
                    placeholder = "Email",
                    onValueChange = { email = it }
                ) {
                    focusManager.clearFocus()
                }
                UnderlinedTextField(
                    content = username,
                    placeholder = "Username",
                    onValueChange = { username = it }
                ) {
                    focusManager.clearFocus()
                }
                UnderlinedTextField(
                    content = password,
                    placeholder = "Password",
                    isSecure = true,
                    onValueChange = { password = it }
                ) {
                    focusManager.clearFocus()
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* TODO Handle Sign Up Click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ), content = {
                    Text(
                        text = "계정 생성",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPagePreview() {
    SignUpPage()
}