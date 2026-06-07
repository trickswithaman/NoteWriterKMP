package com.notewriterkmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.notewriterkmp.notiq.notiq.navigation.MainNavigation
import com.notewriterkmp.notiq.notiq.presentation.NotesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = koinViewModel<NotesListViewModel>()

        MainNavigation(
            viewModel
        )
        /*if (isEditorOpen) {
            NoteEditorScreen(
                note = selectedNote, viewModel = viewModel, onBack = {
                    isEditorOpen = false
                    selectedNote = null
                })
        } else {
            NotesListScreen(viewModel = viewModel, onEdit = { note ->
                selectedNote = note
                isEditorOpen = true
            }, onAdd = {
                selectedNote = null
                isEditorOpen = true
            })
        }*/
    }
}
