package ie.daithi.quizmaster.model

data class Round(
        var index: Int? = null,
        var name: String? = null,
        var questions: List<Question>?
)