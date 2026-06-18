package com.notewriterkmp.notiq.notiq.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.domain.model.getButtonbarItems
import com.notewriterkmp.notiq.notiq.navigation.Screens.Screen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notewriterkmp.notiq.notiq.presentation.SettingScreen.SettingScreen
import notewriterkmp.shared.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    viewModel: NotesListViewModel, onNoteSelected: (NoteEntity?) -> Unit
) {
    val search by viewModel.searchQuery.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        floatingActionButton = {
            if (currentRoute == Screen.NoteListScreen.route) {
                ExtendedFloatingActionButton(
                    onClick = { onNoteSelected(null) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, "Add Note") },
                    text = { Text("New Note") }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentRoute == Screen.NoteListScreen.route) {
                TopSearchBar(search = search, viewModel = viewModel)
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "Notiq Notes",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                          onClick = {}
                        ){
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "",
                                modifier = Modifier.size(25.dp).padding(2.dp)
                            )
                        }
                    },
                )
            }

        },
        bottomBar = {
            ModernBottomBar(navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.NoteListScreen.route,
            ) {
                composable(route = Screen.NoteListScreen.route) {
                    NotesListScreen(
                        viewModel = viewModel,
                        onEdit = { note -> onNoteSelected(note) }
                    )
                }
                composable(route = Screen.SearchScreen.route) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Search feature coming soon", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                composable(route = Screen.AiAssistant.route) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("AI Assistant coming soon", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                composable(route = Screen.Setting.route) {
                    SettingScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    search: String, viewModel: NotesListViewModel
) {
    val isGridView by viewModel.isGridView.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            BasicTextField(
                value = search,
                onValueChange = { viewModel.onSearch(it) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                interactionSource = interactionSource,
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (search.isEmpty()) {
                        Text(
                            "Search your notes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            )

            if (search.isNotEmpty()) {
                IconButton(onClick = { viewModel.onSearch("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { viewModel.toggleViewMode() }) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                    contentDescription = "Toggle view mode",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


        }
    }
}

@Composable
fun ModernBottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val items = remember { getButtonbarItems() }
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
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
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            )
        }
    }
}
