package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.Quiz
import org.springframework.data.mongodb.repository.MongoRepository

interface QuizRepo: MongoRepository<Quiz, String> {
}