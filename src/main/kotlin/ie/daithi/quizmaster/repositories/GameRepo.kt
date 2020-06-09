package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.enumeration.GameStatus
import ie.daithi.quizmaster.model.Game
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface GameRepo: MongoRepository<Game, String> {
    @Query(value = "{ 'players' : ?0, 'status': ?1 }")
    fun findByPlayerIdAndStatus(playerId: String, status: GameStatus): List<Game>
    fun findAllByStatus(status: GameStatus): List<Game>

}