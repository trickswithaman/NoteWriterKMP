package com.notiq.notiq.notiq.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.notiq.components.ModernBottomBar
import com.notiq.notiq.notiq.components.NormalTopBar
import com.notiq.notiq.notiq.components.TopSearchBar
import com.notiq.notiq.notiq.navigation.Screens.Screen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SearchScreen.SearchScreen
import com.notiq.notiq.notiq.presentation.SettingScreen.SettingScreen
import io.github.ismoy.imagepickerkmp.domain.extensions.loadPainter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    viewModel: NotesListViewModel, onNoteSelected: (NoteWithImages?) -> Unit
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
                        viewModel = viewModel, onNoteClick = { note -> onNoteSelected(note) })
                }
                composable(route = Screen.AiAssistant.route) {
                    Text("AI Assistant coming soon...")
                }
                composable(route = Screen.Setting.route) {
                    SettingScreen()
                }
            }
        }
    }
}

@Composable
fun PhotoItem(photo: PhotoResult, modifier: Modifier = Modifier) {
    val painter = photo.loadPainter()
    Card(
        modifier = modifier, shape = RoundedCornerShape(12.dp)
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
