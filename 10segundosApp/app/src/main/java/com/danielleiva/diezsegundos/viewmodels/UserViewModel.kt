package com.danielleiva.diezsegundos.viewmodels

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielleiva.diezsegundos.common.Resource
import com.danielleiva.diezsegundos.models.UserPhoto
import com.danielleiva.diezsegundos.models.requests.UserLoginRequest
import com.danielleiva.diezsegundos.models.responses.LoginResponse
import com.danielleiva.diezsegundos.models.responses.User
import com.danielleiva.diezsegundos.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.internal.wait
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.log

class UserViewModel @Inject constructor(var userRepository: UserRepository): ViewModel() {

    fun login(user : UserLoginRequest) : MutableLiveData<Resource<LoginResponse>> {
        var data : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
        viewModelScope.launch {
            data.value = Resource.Loading()
            val response = userRepository.login(user)
            data.value = handleLoginResponse(response)
        }
        return data
    }

    fun findAll() : MutableLiveData<Resource<List<User>>>{
        var data : MutableLiveData<Resource<List<User>>> = MutableLiveData()

        viewModelScope.launch {
            data.value = Resource.Loading()
            val response = userRepository.findAll()
            data.value = handleLoginResponse(response)
        }
        return data
    }

    fun getUserLogeado() : MutableLiveData<Resource<User>>{
        var data : MutableLiveData<Resource<User>> = MutableLiveData()

        viewModelScope.launch {
            data.value = Resource.Loading()
            val response = userRepository.getUserLogeado()
            data.value = handleLoginResponse(response)
        }
        return data
    }

    fun getImgById(id : String) : MutableLiveData<Resource<ResponseBody>>{
        var data : MutableLiveData<Resource<ResponseBody>> = MutableLiveData()

        viewModelScope.launch {
            data.value = Resource.Loading()
            val response = userRepository.getImgById(id)
            data.value = handleLoginResponse(response)
        }
        return data
    }

    fun signUp(file : MultipartBody.Part?, username : RequestBody, fullname : RequestBody, password : RequestBody, password2: RequestBody) : MutableLiveData<Resource<User>>{
        var data : MutableLiveData<Resource<User>> = MutableLiveData()

        viewModelScope.launch {
            data.value = Resource.Loading()
            val response = userRepository.signUp(file, username, fullname, password, password2)
            data.value = handleLoginResponse(response)
        }
        return data
    }

    fun handleUserResponse(response: Response<List<User>>): Resource<List<UserPhoto>>? {
        if (response.isSuccessful){
            response.body()?.let {
                var usuariosPhoto = ArrayList<UserPhoto>()
                for(usuario in it) {
                    viewModelScope.launch {
                        val responseImgUrl = userRepository.getImgById(usuario.id)
                        if(responseImgUrl.isSuccessful) {
                            Log.i("aquiii","aquii")
                            val bmp =
                                BitmapFactory.decodeStream(responseImgUrl.body()?.byteStream())
                            usuariosPhoto.add(UserPhoto(usuario, bmp))
                        } else {
                            usuariosPhoto.add((UserPhoto(usuario, null)))
                        }
                    }
                }

                return Resource.Success(usuariosPhoto)
            }
        }
        return Resource.Error(response.code().toString())
    }

    fun <T: Any> handleLoginResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.code().toString())
    }
}