package com.notiq.notiq.notiq.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.notiq.notiq.domain.auth.GoogleAuthProvider
import com.notiq.notiq.domain.model.AuthUser
import com.notiq.notiq.domain.model.NoteWithImages
import com.notiq.notiq.notiq.components.ModernBottomBar
import com.notiq.notiq.notiq.components.NormalTopBar
import com.notiq.notiq.notiq.components.TopSearchBar
import com.notiq.notiq.notiq.navigation.Screens.Screen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListScreen
import com.notiq.notiq.notiq.presentation.NoteLIstScreen.NotesListViewModel
import com.notiq.notiq.notiq.presentation.SearchScreen.SearchScreen
import com.notiq.notiq.notiq.presentation.SettingScreen.SettingScreen
import com.notiq.notiq.notiq.presentation.SignIn.SignInViewModel
import com.notiq.notiq.notiq.presentation.SignIn.SignInState
import io.github.ismoy.imagepickerkmp.domain.extensions.loadPainter
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    GoogleAuthUiClient: GoogleAuthProvider,
    viewModel: NotesListViewModel,
    onNoteSelected: (NoteWithImages?) -> Unit
) {
    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val signInViewModel = koinViewModel<SignInViewModel>()
    val state by signInViewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

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
                        viewModel = viewModel, onNoteClick = { note -> onNoteSelected(note) })
                }
                composable(route = Screen.AiAssistant.route) {
                    if (state.userData == null) {
                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                scope.launch {
                                    val result = GoogleAuthUiClient.signIn()
                                    result.onSuccess { signInResult ->
                                        signInViewModel.onSignInResult(signInResult)
                                    }.onFailure { e ->
                                        signInViewModel.onSignInError(e.message)
                                    }
                                }
                            }
                        )
                    } else {
                        ProfileScreen(
                            userData = state.userData,
                            onSignOut = {
                                scope.launch {
                                    GoogleAuthUiClient.signOut()
                                    signInViewModel.onSignOut()
                                }
                            }
                        )
                    }
                }
                composable(route = Screen.Setting.route) {
                    SettingScreen()
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userData: AuthUser?,
    onSignOut: () -> Unit
) {
    println("ProfileScreen: profilePicture: ${userData?.profilePicture}")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userData?.profilePicture != null) {
            AsyncImage(
                model = userData.profilePicture,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit,
                placeholder = rememberVectorPainter(Icons.Default.Person),
                error = rememberVectorPainter(Icons.Default.Person)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (userData?.username != null) {
            Text(
                text = userData.username,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(onClick = onSignOut) {
            Text(text = "Sign out")
        }
    }
}

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            println(error)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onSignInClick) {
            Text(text = "Sign in with Google")
        }
    }
}

@Composable
fun PhotoItem(photo: PhotoResult, modifier: Modifier = Modifier) {
    val painter = photo.loadPainter()
    Card(
        modifier = modifier, shape = RoundedCornerShape(12.dp)
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
