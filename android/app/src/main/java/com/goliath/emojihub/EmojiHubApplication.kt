package com.goliath.emojihub

import android.app.Application
import com.goliath.emojihub.data_sources.LocalStorage
import com.goliath.emojihub.data_sources.SharedLocalStorage
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EmojiHubApplication: Application() {
    companion object {
        lateinit var preferences: LocalStorage
    }

    override fun onCreate() {
        super.onCreate()
        preferences = SharedLocalStorage(applicationContext)
    }
}