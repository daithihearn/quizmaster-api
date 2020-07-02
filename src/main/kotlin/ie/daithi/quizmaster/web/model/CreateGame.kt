package ie.daithi.quizmaster.web.model

data class CreateGame(
        val players: List<String>,
        val quizId: String,
        val name: String
)