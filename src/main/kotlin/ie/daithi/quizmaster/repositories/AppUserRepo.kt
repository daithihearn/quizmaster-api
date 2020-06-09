package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.AppUser
import org.springframework.data.mongodb.repository.MongoRepository

interface AppUserRepo: MongoRepository<AppUser, String> {
    fun findByIdIn(userIds: List<String>): List<AppUser>
}