package com.danielleiva.diezsegundos.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.danielleiva.diezsegundos.api.UserService
import com.danielleiva.diezsegundos.common.MyApp
import com.danielleiva.diezsegundos.models.requests.UserLoginRequest
import com.danielleiva.diezsegundos.models.requests.UserRegisterRequest
import com.danielleiva.diezsegundos.models.responses.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(var userService: UserService) {

    suspend fun login(user : UserLoginRequest) = userService.login(user)

    suspend fun findAll() = userService.getAll()

    suspend fun getImgById(id : String) = userService.getImgById(id)

    suspend fun getUserLogeado() = userService.getUserLogeado()

    suspend fun signUp(file : MultipartBody.Part?, username : RequestBody, fullname : RequestBody, password : RequestBody, password2: RequestBody) = userService.signUp(file, username, fullname, password, password2)

    suspend fun editPuntuacion(puntuacion : Int) = userService.editPuntuacion(puntuacion)
}