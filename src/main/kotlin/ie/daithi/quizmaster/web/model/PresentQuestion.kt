package ie.daithi.quizmaster.web.model

data class PresentQuestion(
        val roundIndex: Int,
        val questionIndex: Int,
        val question: String
)