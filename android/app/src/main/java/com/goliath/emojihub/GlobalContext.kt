package com.goliath.emojihub

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
    throw RuntimeException("")
}