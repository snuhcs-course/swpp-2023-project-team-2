package com.goliath.emojihub.data_sources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

interface LocalStorage {
    var accessToken: String?
    var currentUser: String?
}

@Singleton
class SharedLocalStorage(
    @ApplicationContext private val context: Context
) : LocalStorage {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("EMOJI_HUB", MODE_PRIVATE)

    override var accessToken: String?
        get() = preferences.getString("accessToken", "")
        set(value) = preferences.edit().putString("accessToken", value).apply()

    override var currentUser: String?
        get() = preferences.getString("currentUser", "")
        set(value) = preferences.edit().putString("currentUser", value).apply()
}