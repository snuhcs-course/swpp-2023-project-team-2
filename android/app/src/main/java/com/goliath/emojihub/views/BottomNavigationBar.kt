package com.goliath.emojihub.views

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.goliath.emojihub.R

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    NavHost(navController, startDestination = PageItem.Feed.screenRoute) {
        composable(PageItem.Feed.screenRoute) {
            FeedPage()
        }

        composable(PageItem.Emoji.screenRoute) {
            EmojiPage()
        }

        composable(PageItem.Profile.screenRoute) {
            ProfilePage()
        }
    }
}

sealed class PageItem(
    val icon: Int, val screenRoute: String
) {
    object Feed: PageItem(R.drawable.round_home_24, "FEED")
    object Emoji: PageItem(R.drawable.round_add_reaction_24, "EMOJI")
    object Profile: PageItem(R.drawable.round_settings_24, "PROFILE")
}

val pageItemList = listOf(
    PageItem.Feed,
    PageItem.Emoji,
    PageItem.Profile
)