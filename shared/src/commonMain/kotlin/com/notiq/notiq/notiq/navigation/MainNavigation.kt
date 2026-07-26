package com.notiq.notiq.notiq.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.notiq.navigation.Screens.Screen
import com.notiq.notiq.notiq.util.UiState
import com.notiq.notiq.notiq.presentation.NoteEditAndCreateScreen.NoteAddAndEditScreen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SplashScreen.SplashScreen

@Composable
fun MainNavigation(
    viewModel: NotesListViewModel
) {
    val notesState by viewModel.notes.collectAsStateWithLifecycle()
    val notes = if (notesState is UiState.Success) (notesState as UiState.Success).data else emptyList()

    MainNavigationContent(notes = notes, splashScreen = { onNavigate ->
        SplashScreen(navigateTO = onNavigate)
    }, dashboardScreen = { onNoteSelected ->
        BottomNavigation(viewModel, onNoteSelected = onNoteSelected)
    }, noteListScreen = { onEdit ->
        NotesListScreen(viewModel = viewModel, onEdit = onEdit)
    }, addNoteScreen = { onBack ->
        // Use NoteWithImages model (null for new note)
        NoteAddAndEditScreen(noteWithImages = null, viewModel = viewModel, onBack = onBack)
    }, noteDetailsScreen = { noteWithImages, onBack ->
        // Pass the full NoteWithImages object to the edit screen
        NoteAddAndEditScreen(noteWithImages = noteWithImages, viewModel = viewModel, onBack = onBack)
    })
}

@Composable
fun MainNavigationContent(
    // Update to use the new NoteWithImages domain model
    notes: List<NoteWithImages>,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SplashScreen.route,
    splashScreen: @Composable (onNavigate: () -> Unit) -> Unit,
    dashboardScreen: @Composable (onNoteSelected: (NoteWithImages?) -> Unit) -> Unit,
    noteListScreen: @Composable (onEdit: (NoteWithImages) -> Unit) -> Unit,
    addNoteScreen: @Composable (onBack: () -> Unit) -> Unit,
    noteDetailsScreen: @Composable (noteWithImages: NoteWithImages?, onBack: () -> Unit) -> Unit
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(
            route = Screen.SplashScreen.route, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(250)
                )
            }) {
            splashScreen {
                navController.navigate(Screen.DashboardScreen.route) {
                    popUpTo(Screen.SplashScreen.route) {
                        inclusive = true
                    }
                }
            }
        }
        composable(
            route = Screen.DashboardScreen.route
        ) {
            dashboardScreen { noteWithImages ->
                if (noteWithImages != null) {
                    // Use note.id from the NoteEntity inside NoteWithImages
                    navController.navigate(Screen.NoteDetailsScreen.passId(noteWithImages.note.id))
                } else {
                    navController.navigate(Screen.AddNoteScreen.route)
                }
            }
        }
        composable(route = Screen.NoteListScreen.route) {
            noteListScreen { noteWithImages ->
                navController.navigate(Screen.NoteDetailsScreen.passId(noteWithImages.note.id))
            }
        }
        composable(route = Screen.AddNoteScreen.route) {
            addNoteScreen {
                navController.popBackStack()
            }
        }
        composable(
            route = Screen.NoteDetailsScreen.route, arguments = listOf(navArgument("noteId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.read { getString("noteId") } ?: ""
            // Find the note in our relational model list
            val noteWithImages = notes.find { it.note.id == noteId }

            noteDetailsScreen(noteWithImages) {
                navController.popBackStack()
            }
        }
    }
}
