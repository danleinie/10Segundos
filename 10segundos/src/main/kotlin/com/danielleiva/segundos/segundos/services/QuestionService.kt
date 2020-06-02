package com.danielleiva.segundos.segundos.services

import com.danielleiva.segundos.segundos.dto.CreateQuestionDto
import com.danielleiva.segundos.segundos.models.Question
import com.danielleiva.segundos.segundos.repository.QuestionRepository
import com.danielleiva.segundos.segundos.upload.ImgurImageAttribute
import com.danielleiva.segundos.segundos.upload.ImgurStorageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class QuestionService(
        private val imageStorageService : ImgurStorageService,
        private val repo : QuestionRepository
) {

    fun new(newQuestion : CreateQuestionDto, file: MultipartFile?) : Question{
        var imageAttribute : Optional<ImgurImageAttribute> = Optional.empty()
        if (file != null) {
            if (!file.isEmpty) {
                imageAttribute = imageStorageService.store(file)
            }
        }
        with(newQuestion){
            val questionToSave = Question(enunciado, respuesta, respuestaDetallada, tags)
            questionToSave.img = imageAttribute.orElse(null)
            return repo.save(questionToSave)
        }
    }

    fun randomQuestion() = repo.findOneRandom()

}