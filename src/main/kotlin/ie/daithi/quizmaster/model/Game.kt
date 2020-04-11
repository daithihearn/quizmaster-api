package ie.daithi.quizmaster.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="games")
data class Game (
    @Id
    var id: String? = null,
    var quizId: String? = null,
    var players: List<Player> = emptyList()
)