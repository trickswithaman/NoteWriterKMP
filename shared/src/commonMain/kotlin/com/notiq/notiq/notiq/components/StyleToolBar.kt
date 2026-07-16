package com.notiq.notiq.notiq.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.notiq.notiq.notiq.ui.theme.Surface
import com.notiq.notiq.notiq.util.boldRegex
import com.notiq.notiq.notiq.util.colorRegex
import com.notiq.notiq.notiq.util.getMarkdownMetadata
import com.notiq.notiq.notiq.util.italicRegex
import com.notiq.notiq.notiq.util.underlineRegex


@Composable
fun StyleToolbar(
    modifier: Modifier = Modifier,
    isKeyboardVisible: Boolean,
    lastFocusedField: Int,
    titleValue: TextFieldValue,
    contentValue: TextFieldValue,
    onTitleValueChange: (TextFieldValue) -> Unit,
    onContentValueChange: (TextFieldValue) -> Unit
) {
    //   if (!isKeyboardVisible) return

    var showColorPicker by remember { mutableStateOf(false) }

    val availableColors = remember {
        listOf(
            "#000000", "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF",
            "#FFA500", "#800080", "#A52A2A", "#808080", "#FFFFFF"
        )
    }

    val isEnabled = lastFocusedField != -1
    val currentVal = if (lastFocusedField == 0) titleValue else if (lastFocusedField == 1) contentValue else TextFieldValue("")
    val onValChange = if (lastFocusedField == 0) onTitleValueChange else if (lastFocusedField == 1) onContentValueChange else { _ -> }

    Surface(
        modifier = modifier.fillMaxWidth().imePadding(),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.alpha(if (isEnabled) 1f else 0.5f)) {
            if (isEnabled && showColorPicker) {
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                val icons = remember {
                    listOf(
                        Icons.Default.FormatBold,
                        Icons.Default.FormatItalic,
                        Icons.Default.FormatUnderlined,
                        Icons.Outlined.Photo,
                        Icons.Default.FormatColorText,
                        Icons.Outlined.Mic
                    )
                }

                val activeStyles = remember(currentVal) {
                    listOf(
                        isStyleActive(currentVal, "**"),
                        isStyleActive(currentVal, "_"),
                        isStyleActive(currentVal, "<u>"),
                        false, // Photo
                        isStyleActive(currentVal, "<color="), // FormatColorText
                        false // Mic
                    )
                }

                icons.forEachIndexed { index, icon ->
                    val isSelected = if (index < activeStyles.size) activeStyles[index] else false

                    IconButton(
                        enabled = isEnabled,
                        onClick = {
                            when (index) {
                                0 -> onValChange(toggleStyle(currentVal, "**", "**"))
                                1 -> onValChange(toggleStyle(currentVal, "_", "_"))
                                2 -> onValChange(toggleStyle(currentVal, "<u>", "</u>"))
                                4 -> {
                                    if (isSelected) {
                                        val match = colorRegex.findAll(currentVal.text).find {
                                            currentVal.selection.min >= it.range.first && currentVal.selection.max <= it.range.last + 1
                                        }
                                        if (match != null) {
                                            val colorHex = match.groupValues[1]
                                            onValChange(toggleStyle(currentVal, "<color=$colorHex>", "</color>"))
                                        } else {
                                            showColorPicker = !showColorPicker
                                        }
                                    } else {
                                        showColorPicker = !showColorPicker
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

            if (!isKeyboardVisible) Spacer(Modifier.height(15.dp))
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
    if (text.isEmpty()) return false

    val (regex, openingLen, closingLen) = when {
        prefix == "**" -> Triple(boldRegex, 2, 2)
        prefix == "_" -> Triple(italicRegex, 1, 1)
        prefix == "<u>" -> Triple(underlineRegex, 3, 4)
        prefix.startsWith("<color=") -> Triple(colorRegex, -1, 8)
        else -> return false
    }

    return regex.findAll(text).any { match ->
        val actualOpeningLen = if (openingLen == -1) {
            match.groupValues[1].length + 8
        } else openingLen

        val contentStart = match.range.first + actualOpeningLen
        val contentEnd = match.range.last + 1 - closingLen

        if (selection.collapsed) {
            // Check if cursor is strictly inside the content or at its boundaries
            selection.start in contentStart..contentEnd
        } else {
            // Check if selection is fully contained within the content
            selection.min >= contentStart && selection.max <= contentEnd
        }
    }
}

private fun toggleStyle(value: TextFieldValue, prefix: String, suffix: String): TextFieldValue {
    val text = value.text
    val selection = value.selection
    val start = selection.min
    val end = selection.max

    val (regex, openingLen, closingLen) = when {
        prefix == "**" -> Triple(boldRegex, 2, 2)
        prefix == "_" -> Triple(italicRegex, 1, 1)
        prefix == "<u>" -> Triple(underlineRegex, 3, 4)
        prefix.startsWith("<color=") -> Triple(colorRegex, -1, 8)
        else -> return value
    }

    val match = regex.findAll(text).find { m ->
        val actualOpeningLen = if (openingLen == -1) m.groupValues[1].length + 8 else openingLen
        val contentStart = m.range.first + actualOpeningLen
        val contentEnd = m.range.last + 1 - closingLen

        if (selection.collapsed) {
            start in contentStart..contentEnd
        } else {
            start >= contentStart && end <= contentEnd
        }
    }

    if (match != null) {
        val actualOpeningLen = if (openingLen == -1) match.groupValues[1].length + 8 else openingLen

        // If it's a color tag and the color is different, replace the tag instead of unwrapping
        if (regex == colorRegex && !match.value.startsWith(prefix)) {
            val unwrapped = match.value.substring(actualOpeningLen, match.value.length - closingLen)
            val wrapped = prefix + unwrapped + suffix
            val newText = text.replaceRange(match.range.first, match.range.last + 1, wrapped)
            val diff = prefix.length - actualOpeningLen
            return value.copy(
                text = newText,
                selection = TextRange(start + diff, end + diff)
            )
        }

        val unwrapped = match.value.substring(actualOpeningLen, match.value.length - closingLen)
        val newText = text.replaceRange(match.range.first, match.range.last + 1, unwrapped)
        val newStart = (start - actualOpeningLen).coerceIn(match.range.first, match.range.first + unwrapped.length)
        val newEnd = (end - actualOpeningLen).coerceIn(match.range.first, match.range.first + unwrapped.length)
        return value.copy(text = newText, selection = TextRange(newStart, newEnd))
    } else {
        val selectionText = text.substring(start, end)
        val wrapped = prefix + selectionText + suffix
        return value.copy(
            text = text.replaceRange(start, end, wrapped),
            selection = TextRange(start + prefix.length, start + prefix.length + selectionText.length)
        )
    }
}
