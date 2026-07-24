package com.notiq.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.notiq.notiq.notiq.components.MarkdownVisualTransformation
import com.notiq.notiq.notiq.components.StyleToolbar
import com.notiq.notiq.notiq.navigation.PhotoItem
import com.notiq.notiq.notiq.util.boldRegex
import com.notiq.notiq.notiq.util.colorRegex
import com.notiq.notiq.notiq.util.italicRegex
import com.notiq.notiq.notiq.util.underlineRegex
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAddAndEditContent(
    isPinned: Boolean = false,
    onTogglePin: () -> Unit,
    titleValue: TextFieldValue,
    // We now take a list of image paths instead of a single path.
    imagePaths: List<String> = emptyList(),
    picker: ImagePickerKMPState,
    onImagePathsChange: (List<String>) -> Unit,
    onTitleValueChange: (TextFieldValue) -> Unit,
    contentValue: TextFieldValue,
    onContentValueChange: (TextFieldValue) -> Unit,
    onBack: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val isKeyboardVisible by remember {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }
    var lastFocusedField by remember { mutableIntStateOf(-1) } // -1 for none, 0 for title, 1 for content

    fun onTextValueChange(
        oldValue: TextFieldValue,
        newValue: TextFieldValue,
        update: (TextFieldValue) -> Unit
    ) {
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
                    val regexes = listOf(
                        boldRegex to Pair(2, 2),
                        italicRegex to Pair(1, 1),
                        underlineRegex to Pair(3, 4)
                    )
                    for ((regex, lens) in regexes) {
                        val (opening, closing) = lens
                        val match = regex.findAll(text).find {
                            it.range.last - it.range.first + 1 == opening + closing && cursor >= it.range.first && cursor <= it.range.last + 1
                        }
                        if (match != null) {
                            finalValue = newValue.copy(
                                text = text.removeRange(match.range),
                                selection = TextRange(match.range.first)
                            )
                            return true
                        }
                    }

                    val colorMatch = colorRegex.findAll(text).find {
                        it.groupValues[2].isEmpty() && cursor >= it.range.first && cursor <= it.range.last + 1
                    }
                    if (colorMatch != null) {
                        finalValue = newValue.copy(
                            text = text.removeRange(colorMatch.range),
                            selection = TextRange(colorMatch.range.first)
                        )
                        return true
                    }
                    return false
                }

                findAndRemoveEmptyTag()
            }
        }
        update(finalValue)
    }

    val titleTransformation = remember { MarkdownVisualTransformation() }
    val contentTransformation = remember { MarkdownVisualTransformation() }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ), navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }, actions = {
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }, title = {})
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        bottomBar = {
            StyleToolbar(
                isKeyboardVisible = isKeyboardVisible,
                lastFocusedField = lastFocusedField,
                titleValue = titleValue,
                contentValue = contentValue,
                onGalleryClick = {
                    // Enable multiple image selection in the gallery picker.
                    picker.launchGallery(allowMultiple = true)
                },
                onCameraClick = {
                    picker.launchCamera()
                },
                onTitleValueChange = onTitleValueChange,
                onContentValueChange = onContentValueChange
            )

        }) { paddingValues ->

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            // Display the list of images.
            if (imagePaths.size == 1) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier.padding(bottom = 8.dp)) {
                        PhotoItem(
                            photo = PhotoResult(uri = imagePaths.first()),
                        )
                        IconButton(
                            onClick = { onImagePathsChange(emptyList()) },
                            modifier = Modifier.align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Remove Image",
                                tint = Color.White
                            )
                        }
                    }
                }
            } else if (imagePaths.size > 1) {
                items(imagePaths) { path ->
                    Box {
                        PhotoItem(photo = PhotoResult(uri = path))
                        IconButton(
                            onClick = { onImagePathsChange(imagePaths.filter { it != path }) },
                            modifier = Modifier.align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Remove Image",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                TextField(
                    value = titleValue,
                    onValueChange = { onTextValueChange(titleValue, it, onTitleValueChange) },
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { if (it.isFocused) lastFocusedField = 0 },
                    placeholder = {
                        Text(
                            "Title",
                            style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.outline)
                        )
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
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                TextField(
                    value = contentValue,
                    onValueChange = { onTextValueChange(contentValue, it, onContentValueChange) },
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { if (it.isFocused) lastFocusedField = 1 },
                    placeholder = {
                        Text(
                            "Note content...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.outline)
                        )
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
}
