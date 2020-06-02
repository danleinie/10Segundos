package com.danielleiva.segundos.segundos.models

import com.danielleiva.segundos.segundos.upload.ImgurImageAttribute
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import kotlin.collections.ArrayList

@Entity
data class Question(
        var enunciado : String,
        var respuesta : Boolean,
        var respuestaDetallada : String,
        var tags : ArrayList<String> = ArrayList(),
        var img : ImgurImageAttribute? = null,
        val fechaCreacion: LocalDateTime? = LocalDateTime.now(),
        @Id @GeneratedValue val id : UUID? = null
)