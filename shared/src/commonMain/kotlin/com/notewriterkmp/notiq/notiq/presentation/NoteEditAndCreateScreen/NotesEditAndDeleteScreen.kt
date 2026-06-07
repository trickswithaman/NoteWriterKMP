package com.notewriterkmp.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.presentation.NotesListViewModel


@Composable
fun NoteEditorScreen(
    note: NoteEntity?,
    viewModel: NotesListViewModel ,
    onBack: () -> Unit
) {

    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {

        TextField(value = title, onValueChange = { title = it })
        TextField(value = content, onValueChange = { content = it })

        Button(onClick = {
            viewModel.saveNote(note, title, content)
            onBack()
        }) {
            Text(if (note != null) "Update" else "Save")
        }
    }
}