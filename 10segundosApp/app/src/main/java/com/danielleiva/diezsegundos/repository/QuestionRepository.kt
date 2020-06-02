package com.danielleiva.diezsegundos.repository

import com.danielleiva.diezsegundos.api.QuestionService
import com.danielleiva.diezsegundos.models.requests.RandomQuestionDto
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(var questionService: QuestionService) {

    suspend fun getOneRandom(randomQuestionDto: RequestBody) = questionService.getOneRandom(randomQuestionDto)

}