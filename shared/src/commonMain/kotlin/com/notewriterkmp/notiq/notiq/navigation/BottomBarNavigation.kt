package com.notewriterkmp.notiq.notiq.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assistant
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.navigation.Screens.Screen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    viewModel: NotesListViewModel, onNoteSelected: (NoteEntity?) -> Unit
) {
    val search by viewModel.searchQuery.collectAsState()

    val navController = rememberNavController()

    val interactionSource = remember { MutableInteractionSource() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val items = remember {
        listOf(
            Bottomitem(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                route = Screen.NoteListScren.route
            ),
            Bottomitem(
                title = "Search",
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
                route = Screen.SearchScreen.route
            ),
            Bottomitem(
                title = "Ai Assistant",
                selectedIcon = Icons.Filled.Assistant,
                unselectedIcon = Icons.Outlined.Assistant,
                route = Screen.AiAssistant.route
            ),
            Bottomitem(
                title = "Setting",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                route = Screen.Setting.route
            ),
        )
    }

    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onNoteSelected(null)
            }, modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add, null, modifier = Modifier.size(30.dp)
            )
        }
    }, modifier = Modifier.fillMaxSize(), containerColor = Color(0xFFF8F9FB), topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF8F9FB)
        ), title = {
            BasicTextField(
                value = search,
                onValueChange = { newValue ->
                    viewModel.onSearch(newValue)
                },
                textStyle = TextStyle(
                    fontSize = 16.sp, color = Color(0xFF1A1A1A)
                ),
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp).height(44.dp),
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
                                    unfocusedBorderColor = Transparent,
                                    focusedContainerColor = White,
                                    unfocusedContainerColor = White
                                ),
                                shape = RoundedCornerShape(12.dp),
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    )
                })
        }, navigationIcon = {
            Box(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp).size(40.dp)
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

        })

    }, bottomBar = {
        Surface(
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            shadowElevation = 6.dp,
            tonalElevation = 6.dp,
            color = White
        ) {
            NavigationBar(
                containerColor = Transparent, // 🔥 important
                contentColor = Color.Blue, tonalElevation = 0.dp
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    NavigationBarItem(
                        modifier = Modifier.padding(horizontal = 15.dp),
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationRoute.toString()) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(7.dp)
                            ) {
                                Icon(
                                    imageVector = item.selectedIcon,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = item.title
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = item.title, fontSize = 12.sp
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF6366F1),
                            unselectedIconColor = Color.Black,
                            indicatorColor = Color.Red
                        )
                    )
                }
            }
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            NavHost(
                modifier = Modifier.padding(15.dp),
                navController = navController,
                startDestination = Screen.NoteListScren.route,
            ) {
                composable(route = Screen.NoteListScren.route) {
                    NotesListScreen(
                        viewModel = viewModel, onEdit = { note ->
                            onNoteSelected(note)
                        }

                    )
                }
                composable(
                    route = Screen.SearchScreen.route
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("search")
                    }
                }
                composable(
                    route = Screen.AiAssistant.route
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("Assistant")
                    }
                }
                composable(
                    route = Screen.Setting.route
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("Setting")
                    }
                }

            }
        }
    }
}

data class Bottomitem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
)