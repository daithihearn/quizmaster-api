package ie.daithi.quizmaster.web.model

data class SubmitAnswer(
        val gameId: String,
        val roundId: String,
        val questionId: String,
        val answer: String
)