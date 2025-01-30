package com.abhishek.gomailai

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication: Application() {
    companion object {
        var instance: CoreApplication? = null
        fun getContext(): Context? {
            return instance?.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}