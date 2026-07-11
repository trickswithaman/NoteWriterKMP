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
import com.notiq.db.NoteEntity
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
        NoteAddAndEditScreen(note = null, viewModel = viewModel, onBack = onBack)
    }, noteDetailsScreen = { note, onBack ->
        NoteAddAndEditScreen(note = note, viewModel = viewModel, onBack = onBack)
    })
}

@Composable
fun MainNavigationContent(
    notes: List<NoteEntity>,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SplashScreen.route,
    splashScreen: @Composable (onNavigate: () -> Unit) -> Unit,
    dashboardScreen: @Composable (onNoteSelected: (NoteEntity?) -> Unit) -> Unit,
    noteListScreen: @Composable (onEdit: (NoteEntity) -> Unit) -> Unit,
    addNoteScreen: @Composable (onBack: () -> Unit) -> Unit,
    noteDetailsScreen: @Composable (note: NoteEntity?, onBack: () -> Unit) -> Unit
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
            dashboardScreen { note ->
                if (note != null) {
                    navController.navigate(Screen.NoteDetailsScreen.passId(note.id))
                } else {
                    navController.navigate(Screen.AddNoteScreen.route)
                }
            }
        }
        composable(route = Screen.NoteListScreen.route) {
            noteListScreen { note ->
                navController.navigate(Screen.NoteDetailsScreen.passId(note.id))
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
            val note = notes.find { it.id == noteId }

            noteDetailsScreen(note) {
                navController.popBackStack()
            }
        }
    }
}
