package com.notewriterkmp.notiq.notiq.navigation.Screens

sealed class Screen (val route : String){
    data object SplashScreen : Screen("splash_screen")
    data object DashboardScreen : Screen("dashboard_screen")
    data object NoteDetailsScreen : Screen("note_details_screen")
    data object AddNoteScreen : Screen("add_note_screen")
}