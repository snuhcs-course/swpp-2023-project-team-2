package com.goliath.emojihub.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object Type {
    val Heading = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 24.sp,
        color = Color.DarkGray
    )

    val Body = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp,
        color = Color.DarkGray
    )

    val Button = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 18.sp,
        color = Color.White
    )
}
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)