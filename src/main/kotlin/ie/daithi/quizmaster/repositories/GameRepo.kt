package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.Game
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface GameRepo: MongoRepository<Game, String> {
    @Query(value = "{ 'players.id' : ?0 }")
    fun getByPlayerId(playerId: String): Game

}