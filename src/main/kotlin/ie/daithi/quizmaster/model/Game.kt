package ie.daithi.quizmaster.model

import ie.daithi.quizmaster.enumeration.GameStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="games")
data class Game (
    @Id
    var id: String? = null,
    val name: String,
    var status: GameStatus,
    val quizId: String,
    val quizMasterId: String,
    var players: List<String>,
    var publishedQuestions: List<String> = emptyList(),
    var currentContent: PublishContent? = null
)