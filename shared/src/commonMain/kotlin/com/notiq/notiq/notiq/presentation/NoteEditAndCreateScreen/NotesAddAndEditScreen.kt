package com.notiq.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.util.RichTextState
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
import kotlinx.coroutines.delay

/**
 * NEW Proper Compose implementation
 */
@Composable
fun NoteAddAndEditScreen(
    noteWithImages: NoteWithImages?, 
    viewModel: NotesListViewModel, 
    onBack: () -> Unit
) {
    var currentNoteWithImages by remember(noteWithImages?.note?.id) { mutableStateOf(noteWithImages) }
    
    val titleState = remember { RichTextState() }
    val contentState = remember { RichTextState() }
    
    var isPinned by remember { mutableStateOf(noteWithImages?.note?.isPinned ?: false) }
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

    // Use a flag to ensure we only load from Markdown once per note ID
    var lastLoadedNoteId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(noteWithImages?.note?.id) {
        val note = noteWithImages?.note ?: return@LaunchedEffect
        if (note.id != lastLoadedNoteId) {
            lastLoadedNoteId = note.id
            currentNoteWithImages = noteWithImages
            titleState.fromMarkdown(note.title ?: "")
            contentState.fromMarkdown(note.content ?: "")
            isPinned = note.isPinned
            imagePaths = noteWithImages.images.map { it.uri }
        }
    }

    LaunchedEffect(titleState.value.text, contentState.value.text, isPinned, imagePaths) {
        // Skip auto-save if we haven't loaded the note yet or if it's currently loading
        if (noteWithImages != null && lastLoadedNoteId != noteWithImages.note.id) return@LaunchedEffect

        val currentTitleMarkdown = titleState.toMarkdown()
        val currentContentMarkdown = contentState.toMarkdown()
        
        val hasChanged = currentTitleMarkdown != (currentNoteWithImages?.note?.title ?: "") ||
                currentContentMarkdown != (currentNoteWithImages?.note?.content ?: "") ||
                isPinned != (currentNoteWithImages?.note?.isPinned ?: false) ||
                imagePaths != (currentNoteWithImages?.images?.map { it.uri } ?: emptyList<String>())

        if (!hasChanged) return@LaunchedEffect

        if (currentNoteWithImages == null && titleState.value.text.isBlank() && contentState.value.text.isBlank() && imagePaths.isEmpty()) return@LaunchedEffect

        delay(500L)
        
        viewModel.saveNote(
            existingNoteId = currentNoteWithImages?.note?.id,
            title = currentTitleMarkdown,
            content = currentContentMarkdown,
            imageUris = imagePaths,
            isPinned = isPinned,
            onSuccess = { savedNote ->
                currentNoteWithImages = savedNote
            }
        )
    }

    val handleBack = {
        viewModel.saveNote(
            existingNoteId = currentNoteWithImages?.note?.id,
            title = titleState.toMarkdown(),
            content = contentState.toMarkdown(),
            imageUris = imagePaths,
            isPinned = isPinned
        )
        onBack()
    }

    NoteAddAndEditContent(
        isPinned = isPinned,
        onTogglePin = { isPinned = !isPinned },
        titleState = titleState,
        contentState = contentState,
        imagePaths = imagePaths,
        picker = picker,
        onImagePathsChange = { imagePaths = it },
        onBack = handleBack
    )
}
