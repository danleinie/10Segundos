package com.danielleiva.segundos.segundos.repository

import com.danielleiva.segundos.segundos.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {

    fun findByUsername(username : String) : Optional<User>

    fun findTop100ByOrderByMaximaPuntuacionDesc() : List<User>

}