package ie.daithi.quizmaster.model

data class Round(
        val id: String,
        val name: String,
        val questions: List<Question> = emptyList()
)