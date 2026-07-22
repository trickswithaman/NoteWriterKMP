package com.notiq.notiq.notiq.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.layout.ContentScale
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
import com.notiq.notiq.notiq.components.ModernBottomBar
import com.notiq.notiq.notiq.components.NormalTopBar
import com.notiq.notiq.notiq.components.TopSearchBar
import com.notiq.notiq.notiq.navigation.Screens.Screen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SearchScreen.SearchScreen
import com.notiq.notiq.notiq.presentation.SettingScreen.SettingScreen
import com.notiq.notiq.notiq.ui.theme.Red
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.domain.extensions.loadPainter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP

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
                    ImagePicker()
                }
                composable(route = Screen.Setting.route) {
                    SettingScreen()
                }
            }
        }
    }
}

@Composable
fun ImagePicker() {
    val picker = rememberImagePickerKMP()
    val result = picker.result

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { picker.launchCamera() }, modifier = Modifier.weight(1f)) {
                Text("Camera")
            }
            Button(onClick = { picker.launchGallery() }, modifier = Modifier.weight(1f)) {
                Text("Gallery")
            }
        }

        when (result) {
            is ImagePickerResult.Loading -> CircularProgressIndicator()
            is ImagePickerResult.Success -> {
                val photos = result.photos
                if (photos.size == 1) {
                    PhotoItem(photo = photos.first(), modifier = Modifier.background(Green).wrapContentSize())
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxWidth().weight(1f),
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
            is ImagePickerResult.Idle -> Text("Press a button to get started", color = Gray)
        }
    }
}

@Composable
fun PhotoItem(photo: PhotoResult, modifier: Modifier = Modifier) {
    val painter = photo.loadPainter()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}