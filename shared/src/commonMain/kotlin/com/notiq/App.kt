package com.notiq

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.notiq.notiq.notiq.ui.theme.NoteWriterTheme
import com.notiq.notiq.notiq.navigation.MainNavigation
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SettingScreen.SettingsViewModel
import com.notiq.notiq.domain.auth.GoogleAuthProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val selectedTheme by settingsViewModel.selectedTheme.collectAsStateWithLifecycle()

    val darkTheme = when (selectedTheme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val googleAuthUiClient = koinInject<GoogleAuthProvider>()

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .crossfade(true)
            .build()
    }

    NoteWriterTheme(darkTheme = darkTheme) {
        val viewModel = koinViewModel<NotesListViewModel>()

        MainNavigation(
            viewModel = viewModel,
            googleAuthUiClient = googleAuthUiClient
        )
    }
}
