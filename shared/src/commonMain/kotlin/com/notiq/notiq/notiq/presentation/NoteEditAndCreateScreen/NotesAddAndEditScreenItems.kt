package com.notiq.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.notiq.notiq.notiq.components.RichTextEditor
import com.notiq.notiq.notiq.components.StyleToolbar
import com.notiq.notiq.notiq.navigation.PhotoItem
import com.notiq.notiq.notiq.util.RichTextState
import com.notiq.notiq.notiq.util.getMarkdownMetadata
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState

/**
 * NEW Proper Compose Content Implementation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAddAndEditContent(
    isPinned: Boolean = false,
    onTogglePin: () -> Unit,
    titleState: RichTextState,
    contentState: RichTextState,
    imagePaths: List<String> = emptyList(),
    picker: ImagePickerKMPState,
    onImagePathsChange: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var lastFocusedField by remember { mutableStateOf(-1) }

    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val isKeyboardVisible by remember {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.navigationBars,
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
        bottomBar = {
            StyleToolbar(
                isKeyboardVisible = isKeyboardVisible,
                lastFocusedField = lastFocusedField,
                titleState = titleState,
                contentState = contentState,
                onGalleryClick = { picker.launchGallery(allowMultiple = true) },
                onCameraClick = { picker.launchCamera() }
            )
        }) { paddingValues ->

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            // Images section
            if (imagePaths.size == 1) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier.padding(bottom = 8.dp)) {
                        PhotoItem(photo = PhotoResult(uri = imagePaths.first()))
                        IconButton(
                            onClick = { onImagePathsChange(emptyList()) },
                            modifier = Modifier.align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Remove", tint = Color.White)
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
                            Icon(Icons.Default.Clear, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Title Editor
            item(span = StaggeredGridItemSpan.FullLine) {
                TextField(
                    value = titleState.value,
                    onValueChange = { titleState.updateValue(it) },
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { if (it.isFocused) lastFocusedField = 0 },
                    placeholder = {
                        Text("Title", style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.outline))
                    },
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

            // Content Editor
            item(span = StaggeredGridItemSpan.FullLine) {
                RichTextEditor(
                    state = contentState,
                    modifier = Modifier.fillMaxWidth()
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusChanged { if (it.isFocused) lastFocusedField = 1 },
                    placeholder = "Note content..."
                )
            }
            
            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(Modifier.size(15.dp))
            }
        }
    }
}
