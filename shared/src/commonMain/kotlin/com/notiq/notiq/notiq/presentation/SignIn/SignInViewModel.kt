package com.notiq.notiq.notiq.presentation.SignIn

import androidx.lifecycle.ViewModel
import com.notiq.notiq.domain.auth.GoogleAuthProvider
import com.notiq.notiq.domain.model.AuthUser
import com.notiq.notiq.domain.model.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel(
    private val googleAuthProvider: GoogleAuthProvider
): ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    init {
        // Check for existing user on init
        val currentUser = googleAuthProvider.currentUser()
        if (currentUser != null) {
            onSignInResult(currentUser)
        }
    }

    fun onSignInResult(result: SignInResult) {
        println("SignInViewModel: result user: ${result.user}")
        println("SignInViewModel: profilePicture: ${result.user?.profilePicture}")
        _state.update { it.copy(
            isSignInSuccessful = true,
            signInError = result.errorMessage,
            userData = result.user
        ) }
    }

    fun onSignInError(error: String?) {
        _state.update { it.copy(
            isSignInSuccessful = false,
            signInError = error,
            userData = null
        ) }
    }

    fun onSignOut() {
        _state.update { SignInState() }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val userData: AuthUser? = null
)
