package ie.daithi.quizmaster.web.model

data class CreateGame(
        val playerEmails: List<String>,
        val quizId: String,
        val name: String
)