package ie.daithi.quizmaster.model

data class Player(
        var id: String? = null,
        var answers: List<Answer>? = null
)