package com.goliath.emojihub

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import com.goliath.emojihub.data_sources.BottomNavigationController

val LocalNavController = compositionLocalOf<NavController> {
    throw RuntimeException("")
}

val LocalBottomNavigationController = compositionLocalOf<BottomNavigationController> {
    throw RuntimeException("")
}