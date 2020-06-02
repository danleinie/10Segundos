package com.danielleiva.diezsegundos.models.requests

data class UserRegisterRequest(
    val fullName: String,
    val password: String,
    val password2: String,
    val username: String
)