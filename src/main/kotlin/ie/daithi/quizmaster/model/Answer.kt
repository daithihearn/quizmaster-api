package ie.daithi.quizmaster.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="answers")
data class Answer(
        var id: String? = null,
        val quizId: String,
        val playerId: String,
        val gameId: String,
        val roundIndex: Int,
        val questionIndex: Int,
        val answer: String,
        var score: Float? = null
)