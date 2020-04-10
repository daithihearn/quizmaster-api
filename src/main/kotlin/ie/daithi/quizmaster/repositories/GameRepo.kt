package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.Game
import org.springframework.data.mongodb.repository.MongoRepository

interface GameRepo: MongoRepository<Game, String> {
}