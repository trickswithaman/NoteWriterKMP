package com.notiq.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.notiq.db.NoteEntity
import com.notiq.notiq.notiq.components.MarkdownVisualTransformation
import com.notiq.notiq.notiq.components.StyleToolbar
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.util.boldRegex
import com.notiq.notiq.notiq.util.cleanEmptyTags
import com.notiq.notiq.notiq.util.colorRegex
import com.notiq.notiq.notiq.util.getMarkdownMetadata
import com.notiq.notiq.notiq.util.italicRegex
import com.notiq.notiq.notiq.util.underlineRegex
import kotlinx.coroutines.delay


@Composable
fun NoteAddAndEditScreen(
    note: NoteEntity?, viewModel: NotesListViewModel, onBack: () -> Unit
) {
    var currentNote by remember(note?.id) { mutableStateOf(note) }
    var titleValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(note?.title ?: ""))
    }
    var contentValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(note?.content ?: ""))
    }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }

    var imagePath by remember(note?.id) {
        mutableStateOf(note?.imagePath)
    }

    LaunchedEffect(note) {
        if (note != null) {
            currentNote = note
            if (titleValue.text.isEmpty() && contentValue.text.isEmpty()) {
                titleValue = TextFieldValue(note.title ?: "")
                contentValue = TextFieldValue(note.content ?: "")
                isPinned = note.isPinned
                imagePath = note.imagePath
            }
        }
    }

    LaunchedEffect(titleValue.text, contentValue.text, isPinned) {
        val hasChanged = titleValue.text != (currentNote?.title ?: "") ||
                contentValue.text != (currentNote?.content ?: "") ||
                isPinned != (currentNote?.isPinned ?: false)

        if (!hasChanged) return@LaunchedEffect

        // Don't create a new note if it's completely blank
        if (currentNote == null && titleValue.text.isBlank() && contentValue.text.isBlank() && imagePath.isNullOrBlank()) return@LaunchedEffect

        delay(500L)
        
        val cleanTitle = com.notiq.notiq.notiq.util.cleanEmptyTags(titleValue.text)
        val cleanContent = com.notiq.notiq.notiq.util.cleanEmptyTags(contentValue.text)

        viewModel.saveNote(
            existingNote = currentNote,
            title = cleanTitle,
            content = cleanContent,
            imagePath = imagePath,
            isPinned = isPinned,
            onSuccess = { savedNote ->
                currentNote = savedNote
            }
        )
    }

    LaunchedEffect(imagePath) {
        if (imagePath == currentNote?.imagePath) return@LaunchedEffect
        
        val cleanTitle = com.notiq.notiq.notiq.util.cleanEmptyTags(titleValue.text)
        val cleanContent = com.notiq.notiq.notiq.util.cleanEmptyTags(contentValue.text)

        viewModel.saveNote(
            existingNote = currentNote,
            title = cleanTitle,
            content = cleanContent,
            imagePath = imagePath,
            isPinned = isPinned,
            onSuccess = { savedNote ->
                currentNote = savedNote
            }
        )
    }

    val handleBack = {
        val cleanTitle = com.notiq.notiq.notiq.util.cleanEmptyTags(titleValue.text)
        val cleanContent = com.notiq.notiq.notiq.util.cleanEmptyTags(contentValue.text)
        
        val hasChanged = cleanTitle != (currentNote?.title ?: "") ||
                cleanContent != (currentNote?.content ?: "") ||
                isPinned != (currentNote?.isPinned ?: false) ||
                imagePath != currentNote?.imagePath

        if (hasChanged) {
            viewModel.saveNote(
                existingNote = currentNote,
                title = cleanTitle,
                content = cleanContent,
                imagePath = imagePath,
                isPinned = isPinned,
                onSuccess = { onBack() }
            )
        } else {
            onBack()
        }
    }

    NoteAddAndEditContent(
        isPinned = isPinned,
        onTogglePin = { isPinned = !isPinned },
        titleValue = titleValue,
        imagePath = imagePath,
        onImagePathChange = { imagePath = it },
        onTitleValueChange = { titleValue = it },
        contentValue = contentValue,
        onContentValueChange = { contentValue = it },
        onBack = handleBack
    )
}
