package com.notewriterkmp.notiq.notiq.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.navigation.Screens.Screen
import com.notewriterkmp.notiq.notiq.presentation.NoteEditAndCreateScreen.NoteEditorScreen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notewriterkmp.notiq.notiq.presentation.NotesListViewModel
import com.notewriterkmp.notiq.notiq.presentation.SplashScreen.SplashScreen

@Composable
fun MainNavigation(
    viewModel: NotesListViewModel
){
    var selectedNote by rememberSaveable { mutableStateOf<NoteEntity?>(null) }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ){
        composable(
            route = Screen.SplashScreen.route,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            }
        ){
            SplashScreen(navigateTO = {
                navController.navigate(Screen.DashboardScreen.route){
                    popUpTo (Screen.SplashScreen.route){
                        inclusive = true
                    }
                }
            })
        }
        composable (
            route = Screen.DashboardScreen.route
        ){
            BottomNavigation(
                viewModel, navController, onNoteSelected = { note ->
                    selectedNote = note
                }
            )
        }
        composable(route = Screen.NoteListScren.route){
            NotesListScreen(
                viewModel = viewModel,
                onEdit = { note ->
                    selectedNote = note
                    navController.navigate(Screen.NoteDetailsScreen.route)
                }, onAdd = {
                    selectedNote = null
                    navController.navigate(Screen.AddNoteScreen.route)
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
        composable(route = Screen.NoteDetailsScreen.route) {
            NoteEditorScreen(
                note = selectedNote,
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

    }
}
