package com.goliath.emojihub.data_sources

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.goliath.emojihub.views.PageItem

class BottomNavigationController {
    private val _bottomNavigationDestination = mutableStateOf(PageItem.Feed.screenRoute)

    @Stable
    val currentDestination: MutableState<String> = _bottomNavigationDestination

    fun updateDestination(destination: PageItem) {
        _bottomNavigationDestination.value = destination.screenRoute
    }
}