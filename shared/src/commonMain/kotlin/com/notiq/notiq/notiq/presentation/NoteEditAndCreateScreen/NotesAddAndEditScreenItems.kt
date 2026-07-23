package com.notiq.notiq.notiq.presentation.NoteEditAndCreateScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.notiq.notiq.notiq.components.MarkdownVisualTransformation
import com.notiq.notiq.notiq.components.StyleToolbar
import com.notiq.notiq.notiq.navigation.PhotoItem
import com.notiq.notiq.notiq.ui.theme.Red
import com.notiq.notiq.notiq.ui.theme.White
import com.notiq.notiq.notiq.util.boldRegex
import com.notiq.notiq.notiq.util.colorRegex
import com.notiq.notiq.notiq.util.italicRegex
import com.notiq.notiq.notiq.util.underlineRegex
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAddAndEditContent(
    isPinned : Boolean = false,
    onTogglePin: () -> Unit,
    titleValue: TextFieldValue,
    imagePath: String? = null,
    picker: ImagePickerKMPState,
    onImagePathChange: (String?) -> Unit,
    onTitleValueChange: (TextFieldValue) -> Unit,
    contentValue: TextFieldValue,
    onContentValueChange: (TextFieldValue) -> Unit,
    onBack: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val result = picker.result
/*    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = true,
                selectionLimit = 20
            )
        )
    )

    val result = picker.result*/

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
        modifier = Modifier.fillMaxSize().imePadding(),
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

                    picker.launchGallery()
                },
                onCameraClick = {
                    picker.launchCamera()
                },
                onTitleValueChange = onTitleValueChange,
                onContentValueChange = onContentValueChange
            )

        }
    ) { paddingValues ->


        LazyColumn (modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {

            item {
                val titleTransformation = remember { MarkdownVisualTransformation() }
                val contentTransformation = remember { MarkdownVisualTransformation() }

                when (result) {

                    is ImagePickerResult.Loading -> CircularProgressIndicator()
                    is ImagePickerResult.Success -> {
                        val photos = result.photos
                        if (photos.size == 1) {
                            Box {
                                PhotoItem(
                                    photo = photos.first(),

                                    )
                                IconButton(
                                    onClick = { onImagePathChange(null) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Remove Image", tint = Color.White)
                                }
                            }
                        } else {
                            LazyVerticalStaggeredGrid(
                                modifier = Modifier.fillMaxWidth(),
                                columns = StaggeredGridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalItemSpacing = 8.dp
                            ) {
                                items(photos) { photo ->
                                    PhotoItem(photo = photo)
                                }
                            }
                        }
                    }

                    is ImagePickerResult.Error -> Text("Error: ${result.exception.message}", color = Red)
                    is ImagePickerResult.Dismissed -> Text("Selection cancelled", color = Gray)
                    is ImagePickerResult.Idle -> {
                        if (imagePath != null) {
                            Box {
                                PhotoItem(
                                    photo = io.github.ismoy.imagepickerkmp.domain.models.PhotoResult(uri = imagePath),
                                )
                                IconButton(
                                    onClick = { onImagePathChange(null) },
                                    modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Remove Image", tint = Color.White)
                                }
                            }
                        }
                    }
                }

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
                    modifier = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) lastFocusedField = 1 },
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
}
