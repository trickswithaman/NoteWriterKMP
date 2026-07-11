package com.notiq

import androidx.compose.ui.window.ComposeUIViewController
import com.notiq.notiq.di.initKoinIos

fun MainViewController() = ComposeUIViewController {
    KoinInitializer.initialize()
    App()
}

private object KoinInitializer {
    private var initialized = false
    fun initialize() {
        if (!initialized) {
            initKoinIos()
            initialized = true
        }
    }
}