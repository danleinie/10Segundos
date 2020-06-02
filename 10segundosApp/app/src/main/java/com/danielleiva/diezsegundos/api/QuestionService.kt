package com.danielleiva.diezsegundos.api

import com.danielleiva.diezsegundos.models.requests.RandomQuestionDto
import com.danielleiva.diezsegundos.models.responses.Question
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface QuestionService {

    @Multipart
    @POST("questions/random")
    suspend fun getOneRandom(@Part("questions") randomQuestionDto: RequestBody) : Response<Question>
}