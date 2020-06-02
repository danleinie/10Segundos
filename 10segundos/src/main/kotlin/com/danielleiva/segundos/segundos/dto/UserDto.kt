package com.danielleiva.segundos.segundos.dto

import com.danielleiva.segundos.segundos.models.User
import com.danielleiva.segundos.segundos.upload.ImgurImageAttribute
import java.util.*

data class UserDto(
        var username: String,
        var fullName: String,
        var roles: String,
        var maxPuntuacion: Int,
        var img : String? = null,
        val id: UUID? = null
)

fun User.toUserDto() = UserDto(username,fullName,roles.joinToString(),maximaPuntuacion,img?.id,id)

data class CreateUserDTO(
        var username: String,
        var fullName: String,
        val password: String,
        val password2: String
)