package com.notewriterkmp

import android.app.Application
import com.notewriterkmp.notiq.di.initKoinAndroid

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}