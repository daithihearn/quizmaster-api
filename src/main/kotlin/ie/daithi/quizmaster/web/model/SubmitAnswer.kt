package ie.daithi.quizmaster.web.model

data class SubmitAnswer(
        val roundIndex: Int,
        val questionIndex: Int,
        val answer: String
)