package com.goliath.emojihub

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EmojiHubApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}