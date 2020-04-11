package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.Answer
import org.springframework.data.mongodb.repository.MongoRepository

interface AnswerRepo: MongoRepository<Answer, String> {
}