package ie.daithi.quizmaster.web

data class CreateGame(
        val playerEmails: List<String>,
        val quizId: String
)