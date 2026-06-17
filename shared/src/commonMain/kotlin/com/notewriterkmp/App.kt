package com.notewriterkmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.notewriterkmp.notiq.notiq.ui.theme.NoteWriterTheme
import com.notewriterkmp.notiq.notiq.navigation.MainNavigation
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    NoteWriterTheme {
        val viewModel = koinViewModel<NotesListViewModel>()

        MainNavigation(
            viewModel
        )
    }
}
