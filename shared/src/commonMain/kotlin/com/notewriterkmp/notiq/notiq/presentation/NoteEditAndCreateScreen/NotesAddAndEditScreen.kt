package com.notewriterkmp.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import kotlinx.coroutines.delay


@Composable
fun NoteAddAndEditScreen(
    note: NoteEntity?,
    viewModel: NotesListViewModel,
    onBack: () -> Unit
) {
    var currentNote by remember { mutableStateOf(note) }
    var title by rememberSaveable { mutableStateOf(note?.title ?: "") }
    var content by rememberSaveable { mutableStateOf(note?.content ?: "") }

    LaunchedEffect(note) {
        if (note != null && title.isEmpty() && content.isEmpty()) {
            title = note.title ?: ""
            content = note.content ?: ""
        }
    }

    LaunchedEffect(title, content) {
        // Don't save if everything is empty
        if (title.isBlank() && content.isBlank()) return@LaunchedEffect

        // Don't save if nothing changed
        if (title == (currentNote?.title ?: "") && content == (currentNote?.content ?: "")) return@LaunchedEffect

        // Debounce: wait for 1 second of inactivity before saving
        delay(1000L)

        viewModel.saveNote(currentNote, title, content, onSuccess = { savedNote ->
            currentNote = savedNote
        })
    }

    NoteAddAndEditContent(
        note = currentNote,
        title = title,
        onTitleChange = { title = it },
        content = content,
        onContentChange = { content = it },
        onBack = onBack
    )
}

@Composable
fun NoteAddAndEditContent(
    note: NoteEntity?,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChangeCircle,
                            contentDescription = "Auto-saving",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                title = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = paddingValues.calculateTopPadding())
        ) {
            Spacer(
                modifier = Modifier.size(20.dp)
            )

            TextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Title"
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                )
            )
            Spacer(
                modifier = Modifier.size(20.dp)
            )
            TextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Description"
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}

@Preview
@Composable
fun NoteAddAndEditScreenPreview() {
    MaterialTheme {
        NoteAddAndEditContent(
            note = NoteEntity(
                id = "1",
                title = "",
                content = "This is a sample note content",
                isPinned = false,
                createdAt = 0L,
                updatedAt = 0L
            ),
            title = "Sample Note",
            onTitleChange = {},
            content = "This is a sample note content",
            onContentChange = {},
            onBack = {}
        )
    }
}