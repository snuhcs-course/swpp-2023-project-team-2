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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.BottomSheetController
import com.goliath.emojihub.data_sources.bottomSheet
import com.goliath.emojihub.ui.theme.EmojiHubTheme
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.BottomNavigationBar
import com.goliath.emojihub.views.LoginNavigation
import com.goliath.emojihub.views.components.CustomBottomSheet
import com.goliath.emojihub.views.components.CustomDialog
import com.goliath.emojihub.views.pageItemList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var apiErrorController: ApiErrorController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmojiHubTheme {
                Box(Modifier.fillMaxSize().background(Color.White)) {
                    val token = userViewModel.userState.collectAsState().value?.accessToken
                    val error by apiErrorController.apiErrorState.collectAsState()
                    if (token.isNullOrEmpty()) {
                        LoginView()
                    } else {
                        RootView()
                    }

                    if (!error?.body().isNullOrEmpty()) {
                        CustomDialog(
                            title = "에러",
                            body = error?.body() ?: "",
                            onDismissRequest = { apiErrorController.dismiss() },
                            confirm = { apiErrorController.dismiss() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginView() {
    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        LoginNavigation(navController = navController)
    }
}

@Composable
fun RootView(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val bottomSheet = bottomSheet()

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalBottomSheetController provides bottomSheet
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