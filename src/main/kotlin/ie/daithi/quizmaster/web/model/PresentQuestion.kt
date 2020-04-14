package ie.daithi.quizmaster.web.model

data class PresentQuestion(
        val gameId: String,
        val roundIndex: Int,
        val questionIndex: Int,
        val question: String,
        val imageUri: String?
)