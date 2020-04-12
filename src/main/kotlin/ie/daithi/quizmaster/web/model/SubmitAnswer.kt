package ie.daithi.quizmaster.web.model

data class SubmitAnswer(
        val gameId: String,
        val roundIndex: Int,
        val questionIndex: Int,
        val answer: String
)