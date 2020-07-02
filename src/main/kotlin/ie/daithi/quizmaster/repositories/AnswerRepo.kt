package ie.daithi.quizmaster.repositories

import ie.daithi.quizmaster.model.Answer
import org.springframework.data.mongodb.repository.MongoRepository

interface AnswerRepo: MongoRepository<Answer, String> {

    fun findByGameId(gameId: String): List<Answer>
    fun findByGameIdAndPlayerId(gameId: String, playerId: String): List<Answer>
    fun findByGameIdAndRoundId(gameId: String, roundId: String): List<Answer>
    fun findByGameIdAndRoundIdAndPlayerId(gameId: String, roundId: String, playerId: String): List<Answer>
    fun findByGameIdAndScore(gameId: String, score: Float?): List<Answer>

    fun existsByGameIdAndPlayerId(gameId: String, playerId: String): Boolean
    fun existsByGameIdAndPlayerIdAndRoundIdAndQuestionId(gameId: String, playerId: String, roundId: String, questionId: String): Boolean
    fun findByGameIdAndRoundIdAndQuestionId(gameId: String, roundId: String, questionId: String): List<Answer>
}