package com.notewriterkmp.notiq.notiq.presentation.SettingScreen

import androidx.lifecycle.ViewModel
import com.notewriterkmp.notiq.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    val selectedTheme: StateFlow<String> = repository.selectedTheme

    fun setTheme(theme: String) {
        repository.setTheme(theme)
    }
}
