package com.notewriterkmp.notiq.presentation.NoteLIstScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.presentation.NotesListViewModel

@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel,
    onEdit: (NoteEntity) -> Unit,
    onAdd: () -> Unit
) {

    val notes by viewModel.notes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    Column {

        Button(onClick = { onAdd() }) {
            Text("Add Note")
        }

        LazyColumn {
            items(notes) { note ->

                NoteItem(
                    note = note,
                    onEditNote = {
                        onEdit(note)   // ✅ FIXED
                    }
                )

                Button(
                    onClick = {
                        viewModel.deleteNote(note.id)
                    }
                ) {
                    Text("Delete ${note.title}")
                }
            }
        }
    }
}
@Composable
fun NoteItem(note: NoteEntity, onEditNote : () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onEditNote()
                }
            )
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = note.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}