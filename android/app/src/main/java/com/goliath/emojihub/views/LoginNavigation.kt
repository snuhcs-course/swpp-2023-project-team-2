package com.goliath.emojihub.views

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun LoginNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage()
        }
        composable("signup") {
            SignUpPage()
        }
    }
}