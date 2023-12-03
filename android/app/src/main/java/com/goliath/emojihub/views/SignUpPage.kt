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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.components.CustomDialog
import com.goliath.emojihub.views.components.TopNavigationBar
import com.goliath.emojihub.views.components.UnderlinedTextField
import kotlinx.coroutines.launch

@Composable
fun SignUpPage() {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    val userViewModel = hiltViewModel<UserViewModel>()

    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(verticalArrangement = Arrangement.Top) {
            TopNavigationBar(
                title = "회원가입",
                navigate = { navController.popBackStack() }
            ) {}

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable(interactionSource = interactionSource, indication = null) {
                        focusManager.clearFocus() },
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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (userViewModel.registerUser(email.text, username.text, password.text)) {
                                showDialog = true
                            }
                        }

                    },
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

        if (showDialog) {
            CustomDialog(
                title = "완료",
                body = "계정 생성이 완료되었습니다."
            )
        }
    }
}