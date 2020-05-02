package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.Quiz
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface QuizRepo: MongoRepository<Quiz, String> {

    @Query(value= "{}", fields = "{'rounds': 0}")
    fun getAll(): List<Quiz>
}