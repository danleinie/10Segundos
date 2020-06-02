package com.danielleiva.segundos.segundos

import com.danielleiva.segundos.segundos.models.User
import com.danielleiva.segundos.segundos.repository.QuestionRepository
import com.danielleiva.segundos.segundos.repository.UserRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.CacheControl
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct


@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

@Component
class InitDataComponent(
		val questionRepository: QuestionRepository,
		val userRepository: UserRepository,
		val encoder : PasswordEncoder
		){
	@PostConstruct
	fun initData(){

		//val user1 = User("xleiiva",encoder.encode("1234"),"Deiniel Leiva","USER")
		//userRepository.save(user1)

		/*questionRepository.save(Question(
				"Los leopardos son una especie adaptable al medio ambiente",
				true,
				"Sí, pueden vivir a 43 ºC en el desierto, en zonas pantanosas o incluso a 25 bajo cero",
				"aquivalaimagen.jpg",
				arrayListOf("Naturaleza","Animales")
		))*/
	}
}