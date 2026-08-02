package com.notiq.notiq.domain.auth

import com.notiq.notiq.domain.model.SignInResult
import com.notiq.notiq.domain.model.AuthUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

actual class GoogleAuthProvider actual constructor() {

    private val auth = Firebase.auth

    actual suspend fun signIn(): Result<SignInResult> {
        // TODO: To implement Google Sign-In on iOS, you need to integrate the GoogleSignIn SDK.
        // Once you have the ID Token from the native SDK, use it to sign in to Firebase:
        // val credential = GoogleAuthProvider.credential(idToken = idToken, accessToken = null)
        // val result = auth.signInWithCredential(credential)
        
        return Result.failure(Exception("Google Sign-In on iOS requires native SDK integration."))
    }

    actual suspend fun signOut() {
        auth.signOut()
    }

    actual fun currentUser(): SignInResult? {
        val user = auth.currentUser ?: return null
        return SignInResult(
            user = AuthUser(
                userId = user.uid,
                username = user.displayName,
                userEmail = user.email,
                profilePicture = user.photoURL
            ),
            errorMessage = null
        )
    }
}
