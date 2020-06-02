package com.danielleiva.diezsegundos.models.responses

data class User(
    val fullName: String,
    val id: String,
    val img: String?,
    val maxPuntuacion: Int,
    val roles: String,
    val username: String
)