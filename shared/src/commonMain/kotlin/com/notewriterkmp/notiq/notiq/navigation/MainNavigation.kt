package com.notewriterkmp.notiq.notiq.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.notewriterkmp.db.NoteEntity
import com.notewriterkmp.notiq.notiq.navigation.Screens.Screen
import com.notewriterkmp.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notewriterkmp.notiq.notiq.presentation.NotesListViewModel
import com.notewriterkmp.notiq.notiq.presentation.SplashScreen.SplashScreen

@Composable
fun MainNavigation(
    viewModel: NotesListViewModel
){
    val navController = rememberNavController()
    var selectedNote by remember { mutableStateOf<NoteEntity?>(null) }
    var isEditorOpen by remember { mutableStateOf(false) }

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
            NotesListScreen(
                viewModel = viewModel,
                onEdit = { note ->
                    selectedNote = note
                    isEditorOpen = true
                }, onAdd = {
                    selectedNote = null
                    isEditorOpen = true
                }

            )
        }
    }
}
