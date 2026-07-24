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
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.notiq.components.MarkdownVisualTransformation
import com.notiq.notiq.notiq.components.StyleToolbar
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.util.boldRegex
import com.notiq.notiq.notiq.util.cleanEmptyTags
import com.notiq.notiq.notiq.util.colorRegex
import com.notiq.notiq.notiq.util.getMarkdownMetadata
import com.notiq.notiq.notiq.util.italicRegex
import com.notiq.notiq.notiq.util.underlineRegex
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
import kotlinx.coroutines.delay


@Composable
fun NoteAddAndEditScreen(
    noteWithImages: NoteWithImages?, viewModel: NotesListViewModel, onBack: () -> Unit
) {
    var currentNoteWithImages by remember(noteWithImages?.note?.id) { mutableStateOf(noteWithImages) }
    
    var titleValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(noteWithImages?.note?.title ?: ""))
    }
    var contentValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(noteWithImages?.note?.content ?: ""))
    }
    var isPinned by remember { mutableStateOf(noteWithImages?.note?.isPinned ?: false) }

    // Now we manage the list of image URIs directly from the NoteWithImages relational model.
    var imagePaths by remember(noteWithImages?.note?.id) {
        mutableStateOf(noteWithImages?.images?.map { it.uri } ?: emptyList())
    }

    val picker = rememberImagePickerKMP()
    val pickerResult = picker.result

    LaunchedEffect(pickerResult) {
        if (pickerResult is ImagePickerResult.Success) {
            val newPaths = pickerResult.photos.map { it.uri }
            if (newPaths.isNotEmpty()) {
                imagePaths = (imagePaths + newPaths).distinct()
            }
        }
    }

    LaunchedEffect(noteWithImages) {
        if (noteWithImages != null) {
            currentNoteWithImages = noteWithImages
            if (titleValue.text.isEmpty() && contentValue.text.isEmpty()) {
                titleValue = TextFieldValue(noteWithImages.note.title ?: "")
                contentValue = TextFieldValue(noteWithImages.note.content ?: "")
                isPinned = noteWithImages.note.isPinned
                imagePaths = noteWithImages.images.map { it.uri }
            }
        }
    }

    LaunchedEffect(titleValue.text, contentValue.text, isPinned, imagePaths) {
        val hasChanged = titleValue.text != (currentNoteWithImages?.note?.title ?: "") ||
                contentValue.text != (currentNoteWithImages?.note?.content ?: "") ||
                isPinned != (currentNoteWithImages?.note?.isPinned ?: false) ||
                imagePaths != (currentNoteWithImages?.images?.map { it.uri } ?: emptyList<String>())

        if (!hasChanged) return@LaunchedEffect

        // Don't create a new note if it's completely blank
        if (currentNoteWithImages == null && titleValue.text.isBlank() && contentValue.text.isBlank() && imagePaths.isEmpty()) return@LaunchedEffect

        // Delay to avoid excessive DB writes during typing
        val isTextChange = titleValue.text != (currentNoteWithImages?.note?.title ?: "") || 
                           contentValue.text != (currentNoteWithImages?.note?.content ?: "")
        if (isTextChange) {
            delay(500L)
        }
        
        val cleanTitle = com.notiq.notiq.notiq.util.cleanEmptyTags(titleValue.text)
        val cleanContent = com.notiq.notiq.notiq.util.cleanEmptyTags(contentValue.text)

        viewModel.saveNote(
            existingNoteId = currentNoteWithImages?.note?.id,
            title = cleanTitle,
            content = cleanContent,
            imageUris = imagePaths,
            isPinned = isPinned,
            onSuccess = { savedNote ->
                currentNoteWithImages = savedNote
            }
        )
    }

    val handleBack = {
        val cleanTitle = com.notiq.notiq.notiq.util.cleanEmptyTags(titleValue.text)
        val cleanContent = com.notiq.notiq.notiq.util.cleanEmptyTags(contentValue.text)
        
        val finalImagePaths = if (pickerResult is ImagePickerResult.Success) {
            (imagePaths + pickerResult.photos.map { it.uri }).distinct()
        } else {
            imagePaths
        }

        val hasChanged = cleanTitle != (currentNoteWithImages?.note?.title ?: "") ||
                cleanContent != (currentNoteWithImages?.note?.content ?: "") ||
                isPinned != (currentNoteWithImages?.note?.isPinned ?: false) ||
                finalImagePaths != (currentNoteWithImages?.images?.map { it.uri } ?: emptyList<String>())

        if (hasChanged) {
            viewModel.saveNote(
                existingNoteId = currentNoteWithImages?.note?.id,
                title = cleanTitle,
                content = cleanContent,
                imageUris = finalImagePaths,
                isPinned = isPinned
            )
        }
        onBack()
    }

    NoteAddAndEditContent(
        isPinned = isPinned,
        onTogglePin = { isPinned = !isPinned },
        titleValue = titleValue,
        imagePaths = imagePaths,
        picker = picker,
        onImagePathsChange = { imagePaths = it },
        onTitleValueChange = { titleValue = it },
        contentValue = contentValue,
        onContentValueChange = { contentValue = it },
        onBack = handleBack
    )
}
