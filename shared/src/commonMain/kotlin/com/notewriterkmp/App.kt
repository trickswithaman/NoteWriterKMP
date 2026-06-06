package com.notewriterkmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.presentation.NotesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = koinViewModel<NotesListViewModel>()

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
           NotesListScreen(
               viewModel = viewModel
           )
        }
    }
}

@Composable
fun NotesListScreen(viewModel: NotesListViewModel) {

    val notes by viewModel.notes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    Column {
        var title by remember { mutableStateOf("") }

        TextField(
            value = title,
            onValueChange = { title = it }
        )

        Button(onClick = {
            viewModel.addNote(title)
        }) {
            Text("Save")
        }

        LazyColumn {
            items(notes) { note ->
                NoteItem(note)
                Button(
                    onClick = {
                        viewModel.deleteNote(id = note.id)
                    }
                ){
                    Text("delete ${note.title}")
                }
            }
        }
    }
}
@Composable
fun NoteItem(note: NoteEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
