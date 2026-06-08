package com.notewriterkmp.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.presentation.NotesListViewModel


@Composable
fun NoteEditorScreen(
    note: NoteEntity?,
    viewModel: NotesListViewModel,
    onBack: () -> Unit
) {

    var title by rememberSaveable { mutableStateOf(note?.title ?: "") }
    var content by rememberSaveable { mutableStateOf(note?.content ?: "") }

    LaunchedEffect(note) {
        if (note != null && title.isEmpty() && content.isEmpty()) {
            title = note.title ?: ""
            content = note.content ?: ""
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        TextField(value = title, onValueChange = { title = it })
        TextField(value = content, onValueChange = { content = it })

        Button(onClick = {
            viewModel.saveNote(note, title, content, onSuccess = {
                onBack()
            })
        }) {
            Text(if (note != null) "Update" else "Save")
        }
    }
}