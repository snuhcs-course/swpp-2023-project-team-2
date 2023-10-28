package com.goliath.emojihub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goliath.emojihub.ui.theme.EmojiHubTheme
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.BottomNavigationBar
import com.goliath.emojihub.views.LoginPage
import com.goliath.emojihub.views.pageItemList
import dagger.hilt.android.AndroidEntryPoint

val LocalNavController = compositionLocalOf<NavController> {
    throw RuntimeException("")
}

@AndroidEntryPoint
class RootActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmojiHubTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    val token = userViewModel.userState.collectAsState().value?.accessToken
                    if (token.isNullOrEmpty()) {
                        LoginPage()
                    } else {
                        RootView()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootView(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        Scaffold(
            bottomBar =  {
                BottomNavigation(
                    backgroundColor = Color.White
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    pageItemList.forEach { pageItem ->
                        BottomNavigationItem(
                            selected = currentRoute == pageItem.screenRoute,
                            onClick = { navController.navigate(pageItem.screenRoute) {
                                navController.graph.startDestinationRoute?.let {
                                    popUpTo(it) { saveState = true }
                                }
                                launchSingleTop = true
                                restoreState = true
                            } },
                            icon = {
                                Icon(
                                    painter = painterResource(id = pageItem.icon),
                                    contentDescription = "",
                                    tint = if (currentRoute == pageItem.screenRoute) Color.Black else Color.LightGray)
                            }
                        )
                    }
                }
            }
        ) {
            Box(Modifier.padding(it)) {
                BottomNavigationBar(navController = navController)
            }
        }
    }
}