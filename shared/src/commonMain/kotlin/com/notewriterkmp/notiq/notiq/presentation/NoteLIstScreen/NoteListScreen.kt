package com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.ui.theme.Red
import com.notewriterkmp.notiq.notiq.ui.theme.White
import com.notewriterkmp.notiq.notiq.util.UiState
import com.notewriterkmp.notiq.notiq.util.renderMarkdown
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel, onEdit: (NoteEntity) -> Unit
) {
    val notesState by viewModel.notes.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotes()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = notesState) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is UiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isGridView) 2 else 1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data, key = { it.id }) { note ->
                        NoteItem(
                            note = note,
                            isGridView = isGridView,
                            onEditNote = { onEdit(note) },
                            onDeleteNote = { viewModel.deleteNote(note.id) })
                    }
                }
            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = Red,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { viewModel.loadNotes() }) {
                        Text("Retry")
                    }
                }
            }
            is UiState.Empty -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No notes found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: NoteEntity, onEditNote: () -> Unit, onDeleteNote: () -> Unit, isGridView: Boolean
) {
    val renderedTitle = remember(note.title) {
        renderMarkdown(note.title ?: "")
    }
    val renderedContent = remember(note.content) {
        renderMarkdown(note.content ?: "")
    }
    val formattedDate = remember(note.createdAt) {
        formatDate(note.createdAt)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEditNote),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = renderedTitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = if (isGridView) 2 else 1
                    )
                    if (!isGridView) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = renderedContent,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
                if (!isGridView) {
                    IconButton(onClick = onDeleteNote) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Red
                        )
                    }
                }
            }

            if (isGridView) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = renderedContent, style = MaterialTheme.typography.bodySmall, maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    IconButton(onClick = onDeleteNote, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    return "${
        localDateTime.dayOfMonth.toString().padStart(2, '0')
    }-" + "${localDateTime.monthNumber.toString().padStart(2, '0')}-" + "${localDateTime.year}"
}
