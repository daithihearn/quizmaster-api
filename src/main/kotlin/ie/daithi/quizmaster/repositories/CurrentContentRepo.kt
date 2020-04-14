package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.PublishContent
import org.springframework.data.mongodb.repository.MongoRepository

interface CurrentContentRepo: MongoRepository<PublishContent, String> {
}