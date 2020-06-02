package com.danielleiva.segundos.segundos.dto

import com.danielleiva.segundos.segundos.models.Question
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

data class QuestionDto(
        var enunciado : String,
        var respuesta : Boolean,
        var respuestaDetallada : String,
        var img : String? = null,
        var tags : List<String>,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        val fechaCreacion : LocalDateTime?,
        var id : UUID?
)

fun Question.toQuestionDto() = QuestionDto(enunciado,respuesta,respuestaDetallada,img?.id,tags,fechaCreacion,id)

data class CreateQuestionDto(
        var enunciado : String,
        var respuesta : Boolean,
        var respuestaDetallada : String,
        var tags : ArrayList<String>
)

data class RandomQuestionDto(
        var ids : ArrayList<String>
)