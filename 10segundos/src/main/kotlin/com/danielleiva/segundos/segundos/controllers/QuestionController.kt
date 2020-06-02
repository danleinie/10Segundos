package com.danielleiva.segundos.segundos.controllers

import com.danielleiva.segundos.segundos.dto.CreateQuestionDto
import com.danielleiva.segundos.segundos.dto.QuestionDto
import com.danielleiva.segundos.segundos.dto.RandomQuestionDto
import com.danielleiva.segundos.segundos.dto.toQuestionDto
import com.danielleiva.segundos.segundos.models.Question
import com.danielleiva.segundos.segundos.repository.QuestionRepository
import com.danielleiva.segundos.segundos.services.QuestionService
import com.danielleiva.segundos.segundos.upload.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.net.URL
import java.util.*

@RestController
@RequestMapping("/questions")
class QuestionController(val questionRepository: QuestionRepository, val questionService: QuestionService, val imgurFilesController: ImgurFilesController) {

    @GetMapping("/")
    fun getAll():List<QuestionDto>{
        var result = questionRepository.findAll()

        if (result.isEmpty())
            throw ResponseStatusException(HttpStatus.NOT_FOUND,"There is no question")
        return result.map { question -> question.toQuestionDto() }
    }

    @PostMapping("/")
    fun new(@RequestPart("question") newQuestionDto: CreateQuestionDto,
            @RequestPart("file") file: MultipartFile?) : ResponseEntity<QuestionDto> {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(questionService.new(newQuestionDto,file).toQuestionDto())
        }catch (ex : ImgurBadRequest){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la subida de la imagen")
        }
    }

    @PostMapping("/random")
    fun getOneRandom(@RequestPart("questions") randomQuestionDto : RandomQuestionDto) : QuestionDto{

        var questionFound = questionService.randomQuestion().toQuestionDto()

        return if (questionFound.id.toString() in randomQuestionDto.ids){
            getOneRandom(randomQuestionDto)
        }
        else {
            questionFound.img = questionFound.img?.let { imgurFilesController.getUrl(it).get().toString() }
            return questionFound
        }

    }





}