package ie.daithi.quizmaster.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="answers")
data class Answer(
        var id: String? = null,
        val quizId: String,
        val playerId: String,
        val gameId: String,
        val roundId: String,
        val questionId: String,
        val answer: String,
        var score: Float? = null
)