package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.Answer
import org.springframework.data.mongodb.repository.MongoRepository

interface AnswerRepo: MongoRepository<Answer, String> {

    fun findByGameIdAndScore(gameId: String, score: Float?): List<Answer>

    fun existsByGameIdAndPlayerId(gameId: String, playerId: String): Boolean
    fun existsByGameIdAndPlayerIdAndRoundIdAndQuestionId(playerId: String, gameId: String, roundId: String, questionId: String): Boolean

}