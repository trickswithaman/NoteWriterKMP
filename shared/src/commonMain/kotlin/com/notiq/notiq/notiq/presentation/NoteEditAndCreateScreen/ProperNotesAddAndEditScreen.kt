package com.notiq.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.util.RichTextState
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
import kotlinx.coroutines.delay

/**
 * A demonstration of the Note screen using "Proper Compose" Rich Text logic.
 * This file is separate so you can compare it with the original
 *
 * NotesAddAndEditScreen.kt.
 */
@Composable
fun ProperNotesAddAndEditScreen(
    noteWithImages: NoteWithImages?, 
    viewModel: NotesListViewModel, 
    onBack: () -> Unit
) {
    var currentNoteWithImages by remember(noteWithImages?.note?.id) { mutableStateOf(noteWithImages) }
    
    // Using RichTextState instead of TextFieldValue
    val titleState = remember { RichTextState() }
    val contentState = remember { RichTextState() }
    
    var isPinned by remember { mutableStateOf(noteWithImages?.note?.isPinned ?: false) }
    var imagePaths by remember(noteWithImages?.note?.id) {
        mutableStateOf(noteWithImages?.images?.map { it.uri } ?: emptyList())
    }

    val picker = rememberImagePickerKMP()
    val pickerResult = picker.result

    // Load initial data
    LaunchedEffect(noteWithImages) {
        if (noteWithImages != null) {
            currentNoteWithImages = noteWithImages
            // Convert Markdown from DB to Proper Compose spans
            titleState.fromMarkdown(noteWithImages.note.title ?: "")
            contentState.fromMarkdown(noteWithImages.note.content ?: "")
            isPinned = noteWithImages.note.isPinned
            imagePaths = noteWithImages.images.map { it.uri }
        }
    }

    // Auto-save logic
    LaunchedEffect(titleState.value.text, contentState.value.text, isPinned, imagePaths) {
        val currentMarkdownTitle = titleState.toMarkdown()
        val currentMarkdownContent = contentState.toMarkdown()
        
        val hasChanged = currentMarkdownTitle != (currentNoteWithImages?.note?.title ?: "") ||
                currentMarkdownContent != (currentNoteWithImages?.note?.content ?: "") ||
                isPinned != (currentNoteWithImages?.note?.isPinned ?: false) ||
                imagePaths != (currentNoteWithImages?.images?.map { it.uri } ?: emptyList<String>())

        if (!hasChanged) return@LaunchedEffect

        if (currentNoteWithImages == null && titleState.value.text.isBlank() && contentState.value.text.isBlank() && imagePaths.isEmpty()) return@LaunchedEffect

        // Delay to avoid excessive DB writes
        delay(500L)
        
        viewModel.saveNote(
            existingNoteId = currentNoteWithImages?.note?.id,
            title = currentMarkdownTitle,
            content = currentMarkdownContent,
            imageUris = imagePaths,
            isPinned = isPinned,
            onSuccess = { savedNote ->
                currentNoteWithImages = savedNote
            }
        )
    }

    val handleBack = {
        val finalTitle = titleState.toMarkdown()
        val finalContent = contentState.toMarkdown()
        
        viewModel.saveNote(
            existingNoteId = currentNoteWithImages?.note?.id,
            title = finalTitle,
            content = finalContent,
            imageUris = imagePaths,
            isPinned = isPinned
        )
        onBack()
    }

    NoteAddAndEditContent(
        isPinned = isPinned,
        onTogglePin = { isPinned = !isPinned },
        titleState = titleState, // Pass state objects
        contentState = contentState,
        imagePaths = imagePaths,
        picker = picker,
        onImagePathsChange = { imagePaths = it },
        onBack = handleBack
    )
}
