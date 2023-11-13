package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.ui.theme.Color.White

@Composable
fun TopNavigationBar(
    title: String = "",
    largeTitle: Boolean = true,
    shouldNavigate: Boolean = true,
    navigate: () -> Unit = {},
    actions: @Composable () -> Unit = {},
) {
    Surface(
        color = Color.Transparent,
        shape = RectangleShape,
        elevation = 1.dp,
        modifier = Modifier.height(64.dp),
    ) {
        Box(Modifier.padding(horizontal = 4.dp)) {
            if (largeTitle) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (shouldNavigate) {
                        IconButton(onClick = { navigate() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    }

                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 12.dp))

                    Spacer(modifier = Modifier.weight(1f))

                    actions()
                }
            } else {
                // TopNavigationBar for `PlayEmojiView`
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (shouldNavigate) {
                        IconButton(onClick = { navigate() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                tint = Color.White,
                                contentDescription = ""
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = White)

                    Spacer(modifier = Modifier.weight(1f))

                    Spacer(modifier = Modifier.weight(0.2f))

                    actions()
                }
            }
        }
    }
}