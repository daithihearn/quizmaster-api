package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.AppUser
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface AppUserRepo: MongoRepository<AppUser, String> {
    fun findByIdIn(userIds: List<String>): List<AppUser>
    fun existsBySubject(subject: String): Boolean
    fun findOneBySubject(subject: String): AppUser
    fun findOneByEmail(email: String): Optional<AppUser>
}