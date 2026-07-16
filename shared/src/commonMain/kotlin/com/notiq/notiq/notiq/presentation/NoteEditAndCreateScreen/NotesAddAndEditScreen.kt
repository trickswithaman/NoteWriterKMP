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

    LaunchedEffect(note) {
        if (note != null) {
            currentNote = note
            if (titleValue.text.isEmpty() && contentValue.text.isEmpty()) {
                titleValue = TextFieldValue(note.title ?: "")
                contentValue = TextFieldValue(note.content ?: "")
                isPinned = note.isPinned
            }
        }
    }

    LaunchedEffect(titleValue.text, contentValue.text, isPinned) {
        val hasChanged = titleValue.text != (currentNote?.title ?: "") ||
                contentValue.text != (currentNote?.content ?: "") ||
                isPinned != (currentNote?.isPinned ?: false)

        if (!hasChanged) return@LaunchedEffect

        // Don't create a new note if it's completely blank
        if (currentNote == null && titleValue.text.isBlank() && contentValue.text.isBlank()) return@LaunchedEffect

        delay(500L)
        
        val cleanTitle = com.notiq.notiq.notiq.util.cleanEmptyTags(titleValue.text)
        val cleanContent = com.notiq.notiq.notiq.util.cleanEmptyTags(contentValue.text)
        
        viewModel.saveNote(currentNote, cleanTitle, cleanContent, isPinned, onSuccess = { savedNote ->
            currentNote = savedNote
        })
    }

    val handleBack = {
        val cleanTitle = com.notiq.notiq.notiq.util.cleanEmptyTags(titleValue.text)
        val cleanContent = com.notiq.notiq.notiq.util.cleanEmptyTags(contentValue.text)
        
        if (cleanTitle != (currentNote?.title ?: "") ||
            cleanContent != (currentNote?.content ?: "") ||
            isPinned != (currentNote?.isPinned ?: false)
        ) {
            viewModel.saveNote(currentNote, cleanTitle, cleanContent, isPinned)
        }
        onBack()
    }

    NoteAddAndEditContent(
        isPinned = isPinned,
        onTogglePin = { isPinned = !isPinned },
        titleValue = titleValue,
        onTitleValueChange = { titleValue = it },
        contentValue = contentValue,
        onContentValueChange = { contentValue = it },
        onBack = handleBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAddAndEditContent(
    isPinned : Boolean = false,
    onTogglePin: () -> Unit,
    titleValue: TextFieldValue,
    onTitleValueChange: (TextFieldValue) -> Unit,
    contentValue: TextFieldValue,
    onContentValueChange: (TextFieldValue) -> Unit,
    onBack: () -> Unit
) {
    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val isKeyboardVisible by remember {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }
    var lastFocusedField by remember { mutableIntStateOf(-1) } // -1 for none, 0 for title, 1 for content

    fun onTextValueChange(oldValue: TextFieldValue, newValue: TextFieldValue, update: (TextFieldValue) -> Unit) {
        if (newValue.text == oldValue.text && newValue.selection == oldValue.selection) {
            update(newValue)
            return
        }
        
        var finalValue = newValue
        
        // Cleanup empty tags on deletion or when the cursor moves into/out of them
        if (newValue.text.length < oldValue.text.length) {
            val text = newValue.text
            val sel = newValue.selection
            if (sel.collapsed) {
                val cursor = sel.start
                
                fun findAndRemoveEmptyTag(): Boolean {
                    val regexes = listOf(boldRegex to Pair(2, 2), italicRegex to Pair(1, 1), underlineRegex to Pair(3, 4))
                    for ((regex, lens) in regexes) {
                        val (opening, closing) = lens
                        val match = regex.findAll(text).find { 
                            it.range.last - it.range.first + 1 == opening + closing && 
                            cursor >= it.range.first && cursor <= it.range.last + 1
                        }
                        if (match != null) {
                            finalValue = newValue.copy(text = text.removeRange(match.range), selection = TextRange(match.range.first))
                            return true
                        }
                    }
                    
                    val colorMatch = colorRegex.findAll(text).find { 
                        it.groupValues[2].isEmpty() && cursor >= it.range.first && cursor <= it.range.last + 1
                    }
                    if (colorMatch != null) {
                        finalValue = newValue.copy(text = text.removeRange(colorMatch.range), selection = TextRange(colorMatch.range.first))
                        return true
                    }
                    return false
                }
                
                findAndRemoveEmptyTag()
            }
        }
        update(finalValue)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                title = {}
            )
        },
        bottomBar = {
            StyleToolbar(
                isKeyboardVisible = isKeyboardVisible,
                lastFocusedField = lastFocusedField,
                titleValue = titleValue,
                contentValue = contentValue,
                onTitleValueChange = onTitleValueChange,
                onContentValueChange = onContentValueChange
            )

        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            val titleTransformation = remember { MarkdownVisualTransformation() }
            val contentTransformation = remember { MarkdownVisualTransformation() }

            TextField(
                value = titleValue,
                onValueChange = { onTextValueChange(titleValue, it, onTitleValueChange) },
                modifier = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) lastFocusedField = 0 },
                placeholder = { 
                    Text("Title", style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.outline)) 
                },
                visualTransformation = titleTransformation,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            TextField(
                value = contentValue,
                onValueChange = { onTextValueChange(contentValue, it, onContentValueChange) },
                modifier = Modifier.fillMaxWidth().weight(1f).onFocusChanged { if (it.isFocused) lastFocusedField = 1 },
                placeholder = { 
                    Text("Note content...", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.outline)) 
                },
                visualTransformation = contentTransformation,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
