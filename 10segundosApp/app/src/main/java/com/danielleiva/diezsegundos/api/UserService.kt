package com.danielleiva.diezsegundos.api

import com.danielleiva.diezsegundos.models.requests.UserLoginRequest
import com.danielleiva.diezsegundos.models.requests.UserRegisterRequest
import com.danielleiva.diezsegundos.models.responses.LoginResponse
import com.danielleiva.diezsegundos.models.responses.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @POST("auth/login")
    suspend fun login (@Body user: UserLoginRequest) : Response<LoginResponse>

    @GET("user/")
    suspend fun getAll() : Response<List<User>>

    @GET("files/{id}")
    suspend fun getImgById(@Path("id") id:String) : Response<ResponseBody>

    @GET("user/me")
    suspend fun getUserLogeado() : Response<User>

    @PUT("user/editpuntuacion/{puntuacion}")
    suspend fun editPuntuacion(@Path("puntuacion") puntuacion : Int) : Response<User>

    @Multipart
    @POST("user/")
    suspend fun signUp(@Part file : MultipartBody.Part?,
                       @Part("username") username : RequestBody,
                       @Part("fullname") fullname : RequestBody,
                       @Part("password") password : RequestBody,
                       @Part("password2") password2 : RequestBody ) : Response<User>
}