package com.danielleiva.diezsegundos.models.responses

data class Question(
    val enunciado: String,
    val fechaCreacion: String,
    val id: String,
    val img: String?,
    val respuesta: Boolean,
    val respuestaDetallada: String,
    val tags: ArrayList<String>
)