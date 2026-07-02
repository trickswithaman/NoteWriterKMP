package com.notiq.notiq.notiq.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notiq.db.NoteEntity
import com.notiq.notiq.domain.model.getButtonbarItems
import com.notiq.notiq.notiq.navigation.Screens.Screen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SearchScreen.SearchScreen
import com.notiq.notiq.notiq.presentation.SettingScreen.SettingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    viewModel: NotesListViewModel, onNoteSelected: (NoteEntity?) -> Unit
) {
    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(floatingActionButton = {
        if (currentRoute == Screen.NoteListScreen.route) {
            ExtendedFloatingActionButton(
                onClick = { onNoteSelected(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, "Add Note") },
                text = { Text("New Note") })
        }
    }, containerColor = MaterialTheme.colorScheme.background, topBar = {
        if (currentRoute == Screen.NoteListScreen.route || currentRoute == Screen.SearchScreen.route) {
            TopSearchBar(search = search, viewModel = viewModel)
        } else {
            NormalTopBar()
        }

    }, bottomBar = {
        ModernBottomBar(navController)
    }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.NoteListScreen.route,
            ) {
                composable(route = Screen.NoteListScreen.route) {
                    NotesListScreen(
                        viewModel = viewModel, onEdit = { note -> onNoteSelected(note) })
                }
                composable(route = Screen.SearchScreen.route) {
                    SearchScreen(
                        viewModel = viewModel,
                        onNoteClick = { note -> onNoteSelected(note) }
                    )
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

@Composable
fun NormalTopBar() {
    Surface(
        modifier = Modifier.statusBarsPadding().fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp).padding(2.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Notiq",
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    search: String, viewModel: NotesListViewModel
) {
    val isGridView by viewModel.isGridView.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier.statusBarsPadding().fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 12.dp),
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
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
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
                })

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
        containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected, onClick = {
                if (!isSelected) {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationRoute.toString()) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }, icon = {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title
                )
            }, label = {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }, colors = NavigationBarItemDefaults.colors(
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
