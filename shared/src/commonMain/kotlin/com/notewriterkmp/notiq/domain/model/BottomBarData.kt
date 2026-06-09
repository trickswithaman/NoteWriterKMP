package com.notewriterkmp.notiq.domain.model

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
import com.notewriterkmp.notiq.notiq.navigation.Screens.Screen


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
            route = Screen.NoteListScren.route
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

/*        Surface(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(bottom = 20.dp, start = 16.dp, end = 16.dp),
  ,          shape = RoundedCornerShape(topStart = 40.dp , topEnd = 40.dp),
            color = White,
            shadowElevation = 8.dp
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .height(70.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,

                        modifier = Modifier
                            .background(
                            color = if (isSelected) SecondaryColor.copy(0.6f) else Transparent,
                            shape = RoundedCornerShape(30.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .clickable {
                            navController.navigate(item.route)
                        }

                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )

                        Text(
                            text = item.title,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }*/