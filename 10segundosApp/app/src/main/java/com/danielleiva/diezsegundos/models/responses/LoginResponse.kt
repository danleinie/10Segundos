package com.danielleiva.diezsegundos.models.responses

data class LoginResponse(
    val token: String,
    val user: User
)