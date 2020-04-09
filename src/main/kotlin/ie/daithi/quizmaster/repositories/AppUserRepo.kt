package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.web.security.model.AppUser
import org.springframework.data.mongodb.repository.MongoRepository

interface AppUserRepo: MongoRepository<AppUser, String> {
    fun findByUsernameIgnoreCase(username: String): AppUser?
    fun existsByUsernameIgnoreCase(username: String): Boolean
}