package ie.daithi.quizmaster.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="answers")
data class Answer(
        var id: String? = null,
        var playerId: String? = null,
        var gameId: String? = null,
        var roundIndex: Int? = null,
        var questionIndex: Int? = null,
        var answer: String? = null,
        var result: Boolean? = null
)