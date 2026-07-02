package com.notiq.notiq.di

import android.content.Context
import org.koin.android.ext.koin.androidContext

fun initKoinAndroid(context: Context) {
    initKoin {
        androidContext(context)
    }
}
