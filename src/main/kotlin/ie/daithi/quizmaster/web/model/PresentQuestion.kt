package ie.daithi.quizmaster.web.model

data class PresentQuestion(
        val gameId: String,
        val roundId: String,
        val questionId: String,
        val question: String,
        val imageUri: String?
)