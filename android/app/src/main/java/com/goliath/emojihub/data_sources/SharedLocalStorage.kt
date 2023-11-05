package com.goliath.emojihub.data_sources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class SharedLocalStorage(
    @ApplicationContext private val context: Context
) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("EMOJI_HUB", MODE_PRIVATE)

    var accessToken: String?
        get() = preferences.getString("accessToken", "")
        set(value) = preferences.edit().putString("accessToken", value).apply()
}