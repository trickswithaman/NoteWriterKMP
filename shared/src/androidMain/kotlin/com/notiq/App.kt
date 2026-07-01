package com.notiq

import android.app.Application
import com.notiq.notiq.di.initKoinAndroid

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}