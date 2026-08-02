package com.notiq.notiq.domain.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.notiq.notiq.domain.model.AuthUser
import com.notiq.notiq.domain.model.SignInResult

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider as FirebaseGoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class GoogleAuthProvider actual constructor() : KoinComponent {

    private val context: Context by inject()
    private val credentialManager = CredentialManager.create(context)
    private val auth = Firebase.auth

    actual suspend fun signIn(): Result<SignInResult> {
        return try {

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId("269068614435-kt8t4o16sb69f04o6jlauhtdivsb01q8.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential

            val googleCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            val firebaseCredential =
                FirebaseGoogleAuthProvider.credential(
                    idToken = googleCredential.idToken,
                    accessToken = null
                )

            val firebaseUser =
                auth.signInWithCredential(firebaseCredential).user
                    ?: return Result.failure(Exception("Firebase user is null"))

            Result.success(
                SignInResult(
                    user = AuthUser(
                        userId = firebaseUser.uid,
                        username = firebaseUser.displayName,
                        userEmail = firebaseUser.email,
                        profilePicture = firebaseUser.photoURL
                    ),
                    errorMessage = null
                )
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun signOut() {

        auth.signOut()

        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )

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