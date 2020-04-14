package ie.daithi.quizmaster.web.model

data class QuestionPointer(
        val gameId: String,
        val roundIndex: Int,
        val questionIndex: Int
)