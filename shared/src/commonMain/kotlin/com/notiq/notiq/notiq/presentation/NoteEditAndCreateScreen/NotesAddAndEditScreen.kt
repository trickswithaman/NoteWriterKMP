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
import androidx.compose.material.icons.outlined.FormatLineSpacing
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.notiq.db.NoteEntity
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.util.boldRegex
import com.notiq.notiq.notiq.util.colorRegex
import com.notiq.notiq.notiq.util.getMarkdownMetadata
import com.notiq.notiq.notiq.util.italicRegex
import com.notiq.notiq.notiq.util.lineHeightRegex
import com.notiq.notiq.notiq.util.underlineRegex
import kotlinx.coroutines.delay


@Composable
fun NoteAddAndEditScreen(
    note: NoteEntity?, viewModel: NotesListViewModel, onBack: () -> Unit
) {
    var currentNote by remember { mutableStateOf(note) }
    var titleValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(note?.title ?: ""))
    }
    var contentValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(note?.content ?: ""))
    }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }

    LaunchedEffect(note) {
        if (note != null && titleValue.text.isEmpty() && contentValue.text.isEmpty()) {
            titleValue = TextFieldValue(note.title ?: "")
            contentValue = TextFieldValue(note.content ?: "")
            isPinned = note.isPinned
        }
    }

    LaunchedEffect(titleValue.text, contentValue.text, isPinned) {
        if (titleValue.text.isBlank() && contentValue.text.isBlank()) return@LaunchedEffect
        if (titleValue.text == (currentNote?.title ?: "") && contentValue.text == (currentNote?.content ?: "") && isPinned == (currentNote?.isPinned ?: false)) return@LaunchedEffect

        delay(1000L)
        viewModel.saveNote(currentNote, titleValue.text, contentValue.text, isPinned, onSuccess = { savedNote ->
            currentNote = savedNote
        })
    }

    NoteAddAndEditContent(
        isPinned = isPinned,
        onTogglePin = { isPinned = !isPinned },
        titleValue = titleValue,
        onTitleValueChange = { titleValue = it },
        contentValue = contentValue,
        onContentValueChange = { contentValue = it },
        onBack = onBack
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
    var lastFocusedField by remember { mutableIntStateOf(1) } // 0 for title, 1 for content

    fun onTextValueChange(oldValue: TextFieldValue, newValue: TextFieldValue, update: (TextFieldValue) -> Unit) {
        if (newValue.text == oldValue.text && newValue.selection == oldValue.selection) return
        
        var finalValue = newValue
        if (newValue.text.length < oldValue.text.length) {
            val text = newValue.text
            val sel = newValue.selection
            if (sel.collapsed) {
                val cursor = sel.start
                if (cursor >= 2 && cursor <= text.length - 2 && text.substring(cursor - 2, cursor + 2) == "****") {
                    finalValue = newValue.copy(text = text.removeRange(cursor - 2, cursor + 2), selection = TextRange(cursor - 2))
                } else if (cursor >= 1 && cursor <= text.length - 1 && text.substring(cursor - 1, cursor + 1) == "__") {
                    finalValue = newValue.copy(text = text.removeRange(cursor - 1, cursor + 1), selection = TextRange(cursor - 1))
                } else if (cursor >= 3 && cursor <= text.length - 4 && text.substring(cursor - 3, cursor + 4) == "<u></u>") {
                    finalValue = newValue.copy(text = text.removeRange(cursor - 3, cursor + 4), selection = TextRange(cursor - 3))
                }
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
            val markdownTransformation = remember { MarkdownVisualTransformation() }

            TextField(
                value = titleValue,
                onValueChange = { onTextValueChange(titleValue, it, onTitleValueChange) },
                modifier = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) lastFocusedField = 0 },
                placeholder = { 
                    Text("Title", style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.outline)) 
                },
                visualTransformation = markdownTransformation,
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
                visualTransformation = markdownTransformation,
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

@Composable
fun StyleToolbar(
    isKeyboardVisible: Boolean,
    lastFocusedField: Int,
    titleValue: TextFieldValue,
    contentValue: TextFieldValue,
    onTitleValueChange: (TextFieldValue) -> Unit,
    onContentValueChange: (TextFieldValue) -> Unit
) {
    if (!isKeyboardVisible) return

    var showColorPicker by remember { mutableStateOf(false) }
    var showLineSpacingPicker by remember { mutableStateOf(false) }
    
    val availableColors = remember {
        listOf(
            "#000000", "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF",
            "#FFA500", "#800080", "#A52A2A", "#808080", "#FFFFFF"
        )
    }
    val lineSpacingOptions = remember { listOf(1.0f, 1.2f, 1.5f, 1.8f, 2.0f) }

    val currentVal = if (lastFocusedField == 0) titleValue else contentValue
    val onValChange = if (lastFocusedField == 0) onTitleValueChange else onContentValueChange

    Surface(
        modifier = Modifier.fillMaxWidth().imePadding(),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column {
            if (showColorPicker) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(availableColors) { hex ->
                        val color = try {
                            Color(0xFF000000 or hex.removePrefix("#").toLong(16))
                        } catch (e: Exception) {
                            Color.Gray
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(color, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .clickable {
                                    val newValue = toggleStyle(currentVal, "<color=$hex>", "</color>")
                                    onValChange(newValue)
                                    showColorPicker = false
                                }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }

            if (showLineSpacingPicker) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(lineSpacingOptions) { spacing ->
                        Text(
                            text = "${spacing}x",
                            modifier = Modifier
                                .clickable {
                                    val newValue = toggleStyle(currentVal, "<lh=$spacing>", "</lh>")
                                    onValChange(newValue)
                                    showLineSpacingPicker = false
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icons = remember {
                    listOf(
                        Icons.Default.FormatBold,
                        Icons.Default.FormatItalic,
                        Icons.Default.FormatUnderlined,
                        Icons.Outlined.Photo,
                        Icons.Outlined.FormatLineSpacing,
                        Icons.Default.FormatColorText,
                        Icons.Outlined.Mic
                    )
                }

                val activeStyles = remember(currentVal.text, currentVal.selection) {
                    listOf(
                        isStyleActive(currentVal, "**"),
                        isStyleActive(currentVal, "_"),
                        isStyleActive(currentVal, "<u>"),
                        false, // Photo
                        isStyleActive(currentVal, "<lh="), // FormatLineSpacing
                        isStyleActive(currentVal, "<color="), // FormatColorText
                        false // Mic
                    )
                }

                icons.forEachIndexed { index, icon ->
                    val isSelected = if (index < activeStyles.size) activeStyles[index] else false

                    IconButton(
                        onClick = {
                            when (index) {
                                0 -> onValChange(toggleStyle(currentVal, "**", "**"))
                                1 -> onValChange(toggleStyle(currentVal, "_", "_"))
                                2 -> onValChange(toggleStyle(currentVal, "<u>", "</u>"))
                                4 -> {
                                    if (isSelected) {
                                        val match = lineHeightRegex.findAll(currentVal.text).find { 
                                            currentVal.selection.min >= it.range.first && currentVal.selection.max <= it.range.last + 1 
                                        }
                                        if (match != null) {
                                            val spacing = match.groupValues[1]
                                            onValChange(toggleStyle(currentVal, "<lh=$spacing>", "</lh>"))
                                        } else {
                                            showLineSpacingPicker = !showLineSpacingPicker
                                            showColorPicker = false
                                        }
                                    } else {
                                        showLineSpacingPicker = !showLineSpacingPicker
                                        showColorPicker = false
                                    }
                                }
                                5 -> {
                                    if (isSelected) {
                                        val match = colorRegex.findAll(currentVal.text).find { 
                                            currentVal.selection.min >= it.range.first && currentVal.selection.max <= it.range.last + 1 
                                        }
                                        if (match != null) {
                                            val colorHex = match.groupValues[1]
                                            onValChange(toggleStyle(currentVal, "<color=$colorHex>", "</color>"))
                                        } else {
                                            showColorPicker = !showColorPicker
                                            showLineSpacingPicker = false
                                        }
                                    } else {
                                        showColorPicker = !showColorPicker
                                        showLineSpacingPicker = false
                                    }
                                }
                                else -> {}
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                    ) {
                        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }
}

class MarkdownVisualTransformation : VisualTransformation {
    private var lastText: String? = null
    private var lastResult: TransformedText? = null

    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original == lastText && lastResult != null) return lastResult!!

        val metadata = getMarkdownMetadata(original)

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = 
                metadata.originalToTransformed[offset.coerceIn(0, original.length)]
            override fun transformedToOriginal(offset: Int): Int = 
                metadata.transformedToOriginal[offset.coerceIn(0, metadata.annotatedString.length)]
        }

        val result = TransformedText(metadata.annotatedString, mapping)
        lastText = original
        lastResult = result
        return result
    }
}

private fun isStyleActive(value: TextFieldValue, prefix: String): Boolean {
    val text = value.text
    val selection = value.selection
    val regex = when {
        prefix == "**" -> boldRegex
        prefix == "_" -> italicRegex
        prefix == "<u>" -> underlineRegex
        prefix.startsWith("<color=") -> colorRegex
        prefix.startsWith("<lh=") -> lineHeightRegex
        else -> return false
    }
    return regex.findAll(text).any { 
        selection.min >= it.range.first && selection.max <= it.range.last + 1
    }
}

private fun toggleStyle(value: TextFieldValue, prefix: String, suffix: String): TextFieldValue {
    val text = value.text
    val selection = value.selection
    val start = selection.min
    val end = selection.max

    val regex = when {
        prefix == "**" -> boldRegex
        prefix == "_" -> italicRegex
        prefix == "<u>" -> underlineRegex
        prefix.startsWith("<color=") -> colorRegex
        prefix.startsWith("<lh=") -> lineHeightRegex
        else -> return value
    }

    val match = regex.findAll(text).find { start >= it.range.first && end <= it.range.last + 1 }

    if (match != null) {
        if (selection.collapsed && selection.start == match.range.last + 1 - suffix.length && match.value.length > prefix.length + suffix.length) {
            return value.copy(selection = TextRange(match.range.last + 1))
        }

        val openingTagLength = if (regex == colorRegex || regex == lineHeightRegex) match.groupValues[1].length + (if (regex == colorRegex) 8 else 5) else prefix.length
        val closingTagLength = if (regex == colorRegex || regex == lineHeightRegex) (if (regex == colorRegex) 8 else 5) else suffix.length

        // If it's a different value (color or line height), replace it instead of just unwrapping
        if ((regex == colorRegex || regex == lineHeightRegex) && !match.value.startsWith(prefix)) {
            val unwrapped = match.value.substring(openingTagLength, match.value.length - closingTagLength)
            val wrapped = prefix + unwrapped + suffix
            val newText = text.replaceRange(match.range.first, match.range.last + 1, wrapped)
            val newStart = start - openingTagLength + prefix.length
            val newEnd = end - openingTagLength + prefix.length
            return value.copy(text = newText, selection = TextRange(newStart, newEnd))
        }
        
        val unwrapped = match.value.substring(openingTagLength, match.value.length - closingTagLength)
        val newText = text.replaceRange(match.range.first, match.range.last + 1, unwrapped)
        val newStart = (start - openingTagLength).coerceIn(match.range.first, match.range.first + unwrapped.length)
        val newEnd = (end - openingTagLength).coerceIn(match.range.first, match.range.first + unwrapped.length)
        return value.copy(text = newText, selection = TextRange(newStart, newEnd))
    } else {
        val selectionText = text.substring(start, end)
        val wrapped = prefix + selectionText + suffix
        return value.copy(text = text.replaceRange(start, end, wrapped), selection = TextRange(start + prefix.length, start + prefix.length + selectionText.length))
    }
}
