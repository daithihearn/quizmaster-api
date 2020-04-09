package ie.daithi.quizmaster.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="quizzes")
data class Quiz (
    @Id
    var id: String? = null,
    var name: String? = null,
    var rounds: List<Round>? = null
)