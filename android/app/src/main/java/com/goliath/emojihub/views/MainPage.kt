package com.goliath.emojihub.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.goliath.emojihub.LocalBottomNavigationController
import com.goliath.emojihub.R

@Composable
fun MainPage() {
    val bottomNavigationController = LocalBottomNavigationController.current
    val currentRoute = bottomNavigationController.currentDestination.value

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            when (currentRoute) {
                PageItem.Feed.screenRoute -> FeedPage()
                PageItem.Emoji.screenRoute -> EmojiPage()
                PageItem.Profile.screenRoute -> ProfilePage()
            }
        }

        BottomNavigation(backgroundColor = Color.White) {
            pageItemList.forEach { pageItem ->
                BottomNavigationItem(
                    selected = currentRoute == pageItem.screenRoute,
                    onClick = {
                        bottomNavigationController.updateDestination(pageItem)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = pageItem.icon),
                            contentDescription = "",
                            tint = if (currentRoute == pageItem.screenRoute) Color.Black else Color.LightGray
                        )
                    }
                )
            }
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