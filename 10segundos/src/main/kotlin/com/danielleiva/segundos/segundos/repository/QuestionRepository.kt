package com.danielleiva.segundos.segundos.repository

import com.danielleiva.segundos.segundos.models.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface QuestionRepository : JpaRepository<Question,UUID> {

    @Query(value="SELECT * FROM Question ORDER BY RAND() LIMIT 1", nativeQuery = true)
    fun findOneRandom() : Question

}