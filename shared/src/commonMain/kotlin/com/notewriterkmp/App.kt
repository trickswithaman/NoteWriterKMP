package com.notewriterkmp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.notewriterkmp.notiq.notiq.ui.theme.NoteWriterTheme
import com.notewriterkmp.notiq.notiq.navigation.MainNavigation
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notewriterkmp.notiq.notiq.presentation.SettingScreen.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val selectedTheme by settingsViewModel.selectedTheme.collectAsStateWithLifecycle()

    val darkTheme = when (selectedTheme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    NoteWriterTheme(darkTheme = darkTheme) {
        val viewModel = koinViewModel<NotesListViewModel>()

        MainNavigation(
            viewModel
        )
    }
}
