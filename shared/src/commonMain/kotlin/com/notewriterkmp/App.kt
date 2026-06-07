package com.notewriterkmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.presentation.NoteEditAndCreateScreen.NoteEditorScreen
import com.notewriterkmp.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notewriterkmp.notiq.presentation.NotesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = koinViewModel<NotesListViewModel>()

        var selectedNote by remember { mutableStateOf<NoteEntity?>(null) }
        var isEditorOpen by remember { mutableStateOf(false) }

        if (isEditorOpen) {
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
        }
    }
}

