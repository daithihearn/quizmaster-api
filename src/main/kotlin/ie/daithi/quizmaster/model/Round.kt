package ie.daithi.quizmaster.model

data class Round(
        var name: String? = null,
        var questions: List<Question>?
)