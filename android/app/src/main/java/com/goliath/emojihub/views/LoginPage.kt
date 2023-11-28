package com.goliath.emojihub.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.R
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.components.UnderlinedTextField
import kotlinx.coroutines.launch

@Composable
fun LoginPage() {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val userViewModel = hiltViewModel<UserViewModel>()
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_horizontal),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(68.dp))
            UnderlinedTextField(
                content = username,
                placeholder = "Username",
                onValueChange = { username = it }
            ) {
                focusManager.clearFocus()
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                          userViewModel.login(username.text, password.text)
                      }
                },
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth().height(44.dp),
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
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { navController.navigate(NavigationDestination.SignUp) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "비회원 모드로 시작하기",
                color = Color.DarkGray,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable {
                    navController.navigate(NavigationDestination.OnLogin)
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
