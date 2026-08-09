package com.notiq

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.notiq.notiq.notiq.ui.theme.NoteWriterTheme
import com.notiq.notiq.notiq.navigation.MainNavigation
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SettingScreen.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val selectedTheme by settingsViewModel.selectedTheme.collectAsStateWithLifecycle()

    val darkTheme = when (selectedTheme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    "foujdsfufjdfjfidsji"
    "skjfhjfbdjfdkf"
    NoteWriterTheme(darkTheme = darkTheme) {
        val viewModel = koinViewModel<NotesListViewModel>()

        MainNavigation(
            viewModel
        )
    }
}
/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RichTextComposeTestScreen(onBack: () -> Unit) {
    var lastFocusedField by remember { mutableStateOf(-1) }
    val state = remember { RichTextState("") }
    val title = remember { RichTextState("") }


    Scaffold(
        modifier = Modifier.imePadding(),
        bottomBar = {
            RichTextComposeToolbar(
                state = state,
                lastFocusedField,
                titleState = title,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            RichTextEditor(

                state = title,
                modifier = Modifier.height(100.dp).fillMaxWidth()
                    .onFocusChanged { if (it.isFocused) lastFocusedField = 0 },
                placeholder = "Title"

            )

            RichTextEditor(
                state = state,
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .onFocusChanged { if (it.isFocused) lastFocusedField = 1 },
                placeholder = "Start typing here..."
            )

        }
    }
}

@Composable
fun RichTextComposeToolbar(
    state: RichTextState,
    lastFocusedField: Int,
    titleState: RichTextState,

    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val currentState = if (lastFocusedField == 0) titleState else if (lastFocusedField == 1) state else null


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
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column {
            if ( showColorPicker && currentState != null) {
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
                                .padding(8.dp)

                        ) {
                            Icon(
                                Icons.Default.FormatColorReset,
                                contentDescription = "Clear Color",
                                modifier = Modifier.size(20.dp)

                            )
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
                            modifier = Modifier
                                .size(30.dp)
                                .background(colorValue, CircleShape)
                                .border(width = if (isSelected)3.dp else 1.dp, if (isSelected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(0.5f), CircleShape)
                                .padding(if (isSelected) 4.dp else 2.dp)
                                .clickable {
                                    currentState.toggleStyle(SpanStyle(color = colorValue))
                                    showColorPicker = false
                                }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 0.5.dp)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                styleActions.forEach { action ->
                    val isActive = currentState?.isStyleActive(action.style) ?: false

                    IconButton(
                        onClick = { currentState?.toggleStyle(action.style) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        )
                    ) {
                        Icon(action.icon, contentDescription = action.description)
                    }
                }
                val isAnyColorActive = activeColor != Color.Unspecified
                IconButton(
                    onClick = { showColorPicker = !showColorPicker },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (isAnyColorActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = if (showColorPicker || isAnyColorActive ) MaterialTheme.colorScheme.primaryContainer.copy(if (showColorPicker) 1f else 0.5f) else Color.Transparent
                    )
                ) {
                    Icon(Icons.Default.FormatColorText, contentDescription = "Color",
                        tint = if (isAnyColorActive)activeColor else
                     LocalContentColor.current)
                }
            }
        }
    }
}*/
