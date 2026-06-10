package com.notewriterkmp.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.outlined.FormatLineSpacing
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.notewriterkmp.notiq.notiq.ui.theme.White
import kotlinx.coroutines.delay


@Composable

fun NoteAddAndEditScreen(
    note: NoteEntity?, viewModel: NotesListViewModel, onBack: () -> Unit
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
        if (title == (currentNote?.title ?: "") && content == (currentNote?.content
                ?: "")
        ) return@LaunchedEffect

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
        modifier = Modifier.fillMaxSize().padding(bottom = 30.dp),
        containerColor = White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                containerColor = White
            ), modifier = Modifier.padding(horizontal = 15.dp), navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.size(20.dp)) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }, actions = {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChangeCircle,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }, title = {

            })
        },
        bottomBar = {

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                color = Color.Gray.copy(0.3f)
            )
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.FormatBold,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(25.dp)
                    )

                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.FormatItalic,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(25.dp)
                    )

                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.FormatUnderlined,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(25.dp)
                    )

                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Photo,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(25.dp)
                    )
                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.FormatLineSpacing,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(25.dp)
                    )
                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Default.FormatColorText,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(25.dp)
                    )
                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Mic,
                        contentDescription = "Auto-saving",
                        modifier = Modifier.size(25.dp)
                    )
                }

                FloatingActionButton(
                    shape = RoundedCornerShape(5.dp),
                    onClick = {},
                    modifier = Modifier.size(50.dp),

                    ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Content",
                        modifier = Modifier.size(25.dp)
                    )
                }

            }

        }) { paddingValues ->

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = paddingValues.calculateTopPadding()
            ).wrapContentSize()
        ) {
            Spacer(
                modifier = Modifier.size(25.dp)
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
            onBack = {})
    }
}