package com.danielleiva.segundos.segundos.controllers

import com.danielleiva.segundos.segundos.dto.UserDto
import com.danielleiva.segundos.segundos.dto.toUserDto
import com.danielleiva.segundos.segundos.models.User
import com.danielleiva.segundos.segundos.security.jwt.JwtTokenProvider
import com.danielleiva.segundos.segundos.upload.ImgurStorageServiceImpl
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
class AuthenticationController(
        private val authenticationManager: AuthenticationManager,
        private val jwtTokenProvider: JwtTokenProvider,
        private val imgurStorageServiceImpl: ImgurStorageServiceImpl
) {

    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody loginRequest : LoginRequest) : JwtUserResponse {
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.username, loginRequest.password
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val user = authentication.principal as User
        val jwtToken = jwtTokenProvider.generateToken(authentication)

        return JwtUserResponse(jwtToken, user.toUserDto())

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/me")
    fun me(@AuthenticationPrincipal user : User) : UserDto {
        var userToSend = user.toUserDto()
        userToSend.img = userToSend.img?.let { imgurStorageServiceImpl.getUrl(it).get().toString() }
        return userToSend
    }

}


data class LoginRequest(
        @NotBlank val username : String,
        @NotBlank val password: String
)

data class JwtUserResponse(
        val token: String,
        val user : UserDto
)