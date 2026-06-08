package com.notewriterkmp.notiq.notiq.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.navigation.Screens.Screen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notewriterkmp.notiq.notiq.presentation.NotesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    viewModel: NotesListViewModel,
    onNoteSelected: (NoteEntity?) -> Unit
) {
    val search by viewModel.searchQuery.collectAsState()


    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNoteSelected(null)
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    null,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FB),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FB)
                ),
                title = {
                    BasicTextField(
                        value = search,
                        onValueChange = { newValue ->
                            viewModel.onSearch(newValue)
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A1A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .height(44.dp),
                        interactionSource = interactionSource,
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            OutlinedTextFieldDefaults.DecorationBox(
                                value = search,
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = interactionSource,
                                placeholder = {
                                    Text(
                                        "Search notes...",
                                        fontSize = 14.sp,
                                        color = Color(0xFF8E8E93)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color(0xFF6B72FF)
                                    )
                                },
                                container = {
                                    OutlinedTextFieldDefaults.Container(
                                        enabled = true,
                                        isError = false,
                                        interactionSource = interactionSource,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6B72FF),
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedContainerColor = White,
                                            unfocusedContainerColor = White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            )
                        }
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 8.dp)
                            .size(40.dp)
                            .background(Color(0xFF6B72FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )

                        // Small plus sign overlay
                        Box(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Surface(
                                color = White,
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.size(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF6B72FF),
                                    modifier = Modifier.padding(1.dp)
                                )
                            }
                        }
                    }

                }
            )

        },
        bottomBar = {

        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                modifier = Modifier.padding(15.dp),
                navController = rememberNavController(),
                startDestination = Screen.NoteListScren.route,
            ) {
                composable(route = Screen.NoteListScren.route) {
                    NotesListScreen(
                        viewModel = viewModel,
                        onEdit = { note ->
                            onNoteSelected(note)
                        }

                    )
                }

            }
        }
    }
}
