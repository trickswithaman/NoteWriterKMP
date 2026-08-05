package com.notiq.notiq.domain.auth

import com.notiq.notiq.domain.model.SignInResult
import com.notiq.notiq.domain.model.AuthUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.GoogleAuthProvider as FirebaseGoogleAuthProvider
import cocoapods.GoogleSignIn.*
import platform.UIKit.*
import kotlinx.coroutines.CompletableDeferred

actual class GoogleAuthProvider actual constructor() {

    private val auth = Firebase.auth

    actual suspend fun signIn(): Result<SignInResult> {
        return try {
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                ?: UIApplication.sharedApplication.windows.firstOrNull()?.let { (it as UIWindow).rootViewController }
                ?: return Result.failure(Exception("Root view controller not found"))

            val idToken = signWithGoogle(rootViewController)
            val firebaseCredential = FirebaseGoogleAuthProvider.credential(idToken = idToken, accessToken = null)
            val firebaseUser = auth.signInWithCredential(firebaseCredential).user
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

    private suspend fun signWithGoogle(presentingViewController: UIViewController): String {
        val deferred = CompletableDeferred<String>()
        GIDSignIn.sharedInstance.signInWithPresentingViewController(presentingViewController) { result, error ->
            if (error != null) {
                deferred.completeExceptionally(Exception(error.localizedDescription))
            } else {
                val idToken = result?.user?.idToken?.tokenString
                if (idToken != null) {
                    deferred.complete(idToken)
                } else {
                    deferred.completeExceptionally(Exception("ID Token is null"))
                }
            }
        }
        return deferred.await()
    }

    actual suspend fun signOut() {
        auth.signOut()
        GIDSignIn.sharedInstance.signOut()
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
