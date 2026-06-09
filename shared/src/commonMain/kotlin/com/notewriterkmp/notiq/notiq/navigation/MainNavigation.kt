package com.notewriterkmp.notiq.notiq.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notewriterkmp.notiq.notiq.navigation.Screens.Screen
import com.notewriterkmp.notiq.notiq.presentation.NoteEditAndCreateScreen.NoteEditorScreen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notewriterkmp.notiq.notiq.presentation.SplashScreen.SplashScreen

@Composable
fun MainNavigation(
    viewModel: NotesListViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(
            route = Screen.SplashScreen.route,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            }
        ) {
            SplashScreen(navigateTO = {
                navController.navigate(Screen.DashboardScreen.route) {
                    popUpTo(Screen.SplashScreen.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(
            route = Screen.DashboardScreen.route
        ) {
            BottomNavigation(
                viewModel, onNoteSelected = { note ->
                    if (note != null) {
                        navController.navigate(Screen.NoteDetailsScreen.passId(note.id))
                    } else {
                        navController.navigate(Screen.AddNoteScreen.route)
                    }
                }
            )
        }
        composable(route = Screen.NoteListScren.route) {
            NotesListScreen(
                viewModel = viewModel,
                onEdit = { note ->
                    navController.navigate(Screen.NoteDetailsScreen.passId(note.id))
                }


            )
        }
        composable(route = Screen.AddNoteScreen.route) {
            NoteEditorScreen(
                note = null,
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.NoteDetailsScreen.route,
            arguments = listOf(navArgument("noteId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val notes by viewModel.notes.collectAsState()
            val note = notes.find { it.id == noteId }

            NoteEditorScreen(
                note = note,
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

    }
}
