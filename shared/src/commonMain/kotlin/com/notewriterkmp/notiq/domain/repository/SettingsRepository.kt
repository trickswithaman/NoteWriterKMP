package com.notewriterkmp.notiq.domain.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {
    private val settings: Settings = Settings()
    private val THEME_KEY = "selected_theme"

    private val _selectedTheme = MutableStateFlow(settings.getString(THEME_KEY, "System Default"))
    val selectedTheme = _selectedTheme.asStateFlow()

    fun setTheme(theme: String) {
        _selectedTheme.value = theme
        settings[THEME_KEY] = theme
    }
}
