package com.goliath.emojihub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.BottomNavigationController
import com.goliath.emojihub.ui.theme.EmojiHubTheme
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.viewmodels.PostViewModel
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.CreatePostPage
import com.goliath.emojihub.views.LoginPage
import com.goliath.emojihub.views.MainPage
import com.goliath.emojihub.views.SignUpPage
import com.goliath.emojihub.views.TransformVideoPage
import com.goliath.emojihub.views.components.CustomDialog
import com.goliath.emojihub.views.components.PlayEmojiView
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
                    val accessToken = userViewModel.accessTokenState.collectAsState().value
                    val error by apiErrorController.apiErrorState.collectAsState()

                    RootView(startDestination =
                        if (accessToken.isNullOrEmpty()) NavigationDestination.Onboard
                        else NavigationDestination.MainPage
                    )

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

    private fun NavGraphBuilder.onboardGraph() {
        navigation(
            startDestination = NavigationDestination.Login,
            route = NavigationDestination.Onboard,
        ) {
            composable(NavigationDestination.Login) {
                LoginPage()
            }
            composable(NavigationDestination.SignUp) {
                SignUpPage()
            }
        }
    }

    @Composable
    fun RootView(startDestination: String) {
        val navController = rememberNavController()
        val bottomNavigationController = remember {
            BottomNavigationController()
        }

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalBottomNavigationController provides bottomNavigationController
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                onboardGraph()

                composable(NavigationDestination.MainPage) {
                    MainPage()
                }

                composable(NavigationDestination.TransformVideo) {
                    val emojiViewModel = hiltViewModel<EmojiViewModel>()
                    TransformVideoPage(emojiViewModel)
                }

                composable(NavigationDestination.PlayEmojiVideo) {
                    val emojiViewModel = hiltViewModel<EmojiViewModel>()
                    PlayEmojiView(emojiViewModel)
                }

                composable(NavigationDestination.CreatePost) {
                    val postViewModel = hiltViewModel<PostViewModel>()
                    CreatePostPage(postViewModel)
                }
            }
        }
    }
}