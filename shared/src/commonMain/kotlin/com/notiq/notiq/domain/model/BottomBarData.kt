package com.notiq.notiq.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assistant
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.notiq.notiq.notiq.navigation.Screens.Screen


data class Bottomitem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
)

fun getButtonbarItems(): ArrayList<Bottomitem>{
    return arrayListOf(
        Bottomitem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = Screen.NoteListScreen.route
        ),
        Bottomitem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
            route = Screen.SearchScreen.route
        ),
        Bottomitem(
            title = "Assistant",
            selectedIcon = Icons.Filled.Assistant,
            unselectedIcon = Icons.Outlined.Assistant,
            route = Screen.AiAssistant.route
        ),
        Bottomitem(
            title = "Setting",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = Screen.Setting.route
        )
    )
}