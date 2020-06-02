package com.danielleiva.diezsegundos.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.models.requests.RandomQuestionDto
import com.danielleiva.diezsegundos.models.responses.Question
import com.danielleiva.diezsegundos.repository.QuestionRepository
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class QuestionViewModel @Inject constructor(var questionRepository: QuestionRepository) : ViewModel() {

    fun getOneRandom(randomQuestionDto: RequestBody) : MutableLiveData<Resource<Question>>{
        var data : MutableLiveData<Resource<Question>> = MutableLiveData()

        viewModelScope.launch {
            data.value = Resource.Loading()
            val response = questionRepository.getOneRandom(randomQuestionDto)
            data.value = handleResponse(response)
        }
        return data

    }

}



fun <T: Any> handleResponse(response: Response<T>): Resource<T>? {
    if (response.isSuccessful){
        response.body()?.let {
            return Resource.Success(it)
        }
    }
    return Resource.Error(response.code().toString())
}