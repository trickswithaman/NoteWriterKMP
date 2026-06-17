package com.notewriterkmp.notiq.notiq.navigation.Screens

sealed class Screen (val route : String){
    data object SplashScreen : Screen("splash_screen")
    data object DashboardScreen : Screen("dashboard_screen")
    data object NoteListScreen : Screen("note_list_screen")
    data object NoteDetailsScreen : Screen("note_details_screen/{noteId}") {
        fun passId(id: String) = "note_details_screen/$id"
    }
    data object SearchScreen : Screen("search_screen")
    data object AiAssistant : Screen("ai_assistant")
    data object Setting : Screen("settings_screen")

    data object AddNoteScreen : Screen("add_note_screen")
}