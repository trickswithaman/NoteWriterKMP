package com.notiq.notiq.notiq.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.notiq.notiq.notiq.util.*

/**
 * Proper Compose Style Toolbar
 */
@Composable
fun StyleToolbar(
    modifier: Modifier = Modifier,
    isKeyboardVisible: Boolean,
    lastFocusedField: Int,
    titleState: RichTextState,
    contentState: RichTextState,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showImageBottomSheet by remember { mutableStateOf(false) }

    val isEnabled = lastFocusedField != -1
    val currentState = if (lastFocusedField == 0) titleState else if (lastFocusedField == 1) contentState else null
    val activeColor = currentState?.getActiveColor() ?: Color.Unspecified

    val styleActions = remember {
        listOf(
            ComposeStyleAction(Icons.Default.FormatBold, BoldStyle, "Bold"),
            ComposeStyleAction(Icons.Default.FormatItalic, ItalicStyle, "Italic"),
            ComposeStyleAction(Icons.Default.FormatUnderlined, UnderlineStyle, "Underline")
        )
    }

    val availableColors = remember {
        listOf(
            "#000000", "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF",
            "#FFA500", "#800080", "#A52A2A", "#808080", "#FFFFFF"
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth().imePadding(),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(bottom = if (!isKeyboardVisible) 15.dp else 0.dp)) {
            if (isEnabled && showColorPicker && currentState != null) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        IconButton(
                            onClick = {
                                currentState.removeColorStyle()
                                showColorPicker = false
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.FormatColorReset, contentDescription = "Clear Color", modifier = Modifier.size(20.dp))
                        }
                    }

                    items(availableColors) { hex ->
                        val colorValue = try {
                            Color(0xFF000000 or hex.removePrefix("#").toLong(16))
                        } catch (e: Exception) {
                            Color.Gray
                        }
                        val isSelected = activeColor == colorValue
                        
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.Transparent, CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .padding(if (isSelected) 4.dp else 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorValue, CircleShape)
                                    .clickable {
                                        currentState.toggleStyle(SpanStyle(color = colorValue))
                                    }
                            )
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (colorValue == Color.White || hex == "#FFFFFF") Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                styleActions.forEach { action ->
                    val isActive = currentState?.isStyleActive(action.style) ?: false
                    IconButton(
                        enabled = isEnabled,
                        onClick = { currentState?.toggleStyle(action.style) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                    ) {
                        Icon(action.icon, contentDescription = action.description, modifier = Modifier.size(22.dp))
                    }
                }

                IconButton(
                    enabled = isEnabled,
                    onClick = { showImageBottomSheet = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Outlined.Photo, contentDescription = "Add Image", modifier = Modifier.size(22.dp))
                }

                val isAnyColorActive = activeColor != Color.Unspecified
                IconButton(
                    enabled = isEnabled,
                    onClick = { showColorPicker = !showColorPicker },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (isAnyColorActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = if (showColorPicker || isAnyColorActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (showColorPicker) 1f else 0.5f) else Color.Transparent
                    )
                ) {
                    Icon(
                        Icons.Default.FormatColorText, 
                        contentDescription = "Color", 
                        modifier = Modifier.size(22.dp),
                        tint = if (isAnyColorActive) activeColor else LocalContentColor.current
                    )
                }

                IconButton(
                    enabled = isEnabled,
                    onClick = { /* Mic Logic */ },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Outlined.Mic, contentDescription = "Voice Note", modifier = Modifier.size(22.dp))
                }
            }

            if (showImageBottomSheet) {
                ImageBottomSheet(
                    onCameraClick = {
                        showImageBottomSheet = false
                        onCameraClick()
                    },
                    onGalleryClick = {
                        showImageBottomSheet = false
                        onGalleryClick()
                    },
                    onDismiss = {
                        showImageBottomSheet = false
                    }
                )
            }
        }
    }
}

data class ComposeStyleAction(
    val icon: ImageVector,
    val style: SpanStyle,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCameraClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Camera")
            }

            Button(
                onClick = onGalleryClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Gallery")
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
