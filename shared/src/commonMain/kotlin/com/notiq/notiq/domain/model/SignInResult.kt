package com.notiq.notiq.domain.model

data class SignInResult (
    val user : AuthUser,
    val errorMessage : String?
)

data class AuthUser (
    val userId : String,
    val username : String?,
    val userEmail : String?,
    val profilePicture : String?
)