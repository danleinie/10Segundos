package com.danielleiva.segundos.segundos.controllers

import com.danielleiva.segundos.segundos.dto.CreateUserDTO
import com.danielleiva.segundos.segundos.dto.UserDto
import com.danielleiva.segundos.segundos.dto.toUserDto
import com.danielleiva.segundos.segundos.repository.UserRepository
import com.danielleiva.segundos.segundos.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/user")
class UserController(val userService: UserService) {

    @GetMapping("/")
    fun findAllOrderByMaxPuntuacion() : List<UserDto> {
        var result = userService.findTop100ByOrderByMaximaPuntuacionDesc()
        if (result.isEmpty())
            throw ResponseStatusException(HttpStatus.NOT_FOUND,"There is no users")
        println(result)
        return result.map { user -> user.toUserDto() }
    }

    /*@PostMapping("/")
    fun nuevoUsuario(@RequestPart("user") newUser : CreateUserDTO, @RequestPart("file") file: MultipartFile): ResponseEntity<UserDto> =
            userService.create(newUser,file).map { ResponseEntity.status(HttpStatus.CREATED).body(it.toUserDto()) }.orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ${newUser.username} ya existe")
            }*/

    @PostMapping("/")
    fun nuevoUsuario(@RequestPart("username") username : String,
                     @RequestPart("fullname") fullname : String,
                     @RequestPart("password") password : String,
                     @RequestPart("password2") password2 : String,
                     @RequestPart("file") file: MultipartFile?): ResponseEntity<UserDto> =
            userService.create(CreateUserDTO(username,fullname,password,password2),file).map { ResponseEntity.status(HttpStatus.CREATED).body(it.toUserDto()) }.orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario $username ya existe")
            }




}