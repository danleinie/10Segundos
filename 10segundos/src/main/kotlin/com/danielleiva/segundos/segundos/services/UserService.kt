package com.danielleiva.segundos.segundos.services

import com.danielleiva.segundos.segundos.dto.CreateUserDTO
import com.danielleiva.segundos.segundos.models.User
import com.danielleiva.segundos.segundos.repository.UserRepository
import com.danielleiva.segundos.segundos.upload.ImgurImageAttribute
import com.danielleiva.segundos.segundos.upload.ImgurStorageService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class UserService(
        private val repo: UserRepository,
        private val encoder: PasswordEncoder,
        private val imageStorageService: ImgurStorageService

) {

    fun create(newUser : CreateUserDTO, file: MultipartFile?): Optional<User> {
        if (findByUsername(newUser.username).isPresent)
            return Optional.empty()
        return Optional.of(
                with(newUser) {
                    var imageAttribute : Optional<ImgurImageAttribute> = Optional.empty()
                    if (file != null) {
                        if (!file.isEmpty) {
                            imageAttribute = imageStorageService.store(file)
                        }
                    }
                    val userToSave = User(username, encoder.encode(password), fullName, "USER")
                    //val rnds = (0..25).random()
                    //userToSave.maximaPuntuacion = rnds
                    userToSave.img = imageAttribute.orElse(null)
                    repo.save(userToSave)
                }

        )
    }

    fun findTop100ByOrderByMaximaPuntuacionDesc() = repo.findTop100ByOrderByMaximaPuntuacionDesc()

    fun findByUsername(username : String) = repo.findByUsername(username)

    fun findById(id : UUID) = repo.findById(id)

}