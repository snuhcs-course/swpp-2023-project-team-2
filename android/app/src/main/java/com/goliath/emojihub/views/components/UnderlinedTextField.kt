package com.goliath.emojihub.views.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.goliath.emojihub.ui.theme.Color

@Composable
fun UnderlinedTextField(
    content: TextFieldValue,
    placeholder: String,
    isSecure: Boolean = false,
    onValueChange: (TextFieldValue) -> Unit,
    keyboardActions: () -> Unit = {}
) {
    TextField(
        value = content,
        onValueChange = { onValueChange(it) },
        placeholder = {
            Text(
                text = placeholder,
                color = Color.LightGray
            )
        },
        modifier = Modifier
            .testTag(placeholder+"Field")
            .onFocusChanged { it.isFocused }
            .fillMaxWidth(),
        keyboardActions = KeyboardActions(
            onDone = { keyboardActions() }
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.LightGray,
        ),
        visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}