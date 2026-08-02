package com.notiq.notiq.domain.auth

import com.notiq.notiq.domain.model.SignInResult

expect class GoogleAuthProvider() {

    suspend fun signIn(): Result<SignInResult>

    suspend fun signOut()

    fun currentUser(): SignInResult?
}