package ie.daithi.quizmaster.model

data class Answer(
        var roundIndex: Int? = null,
        var questionIndex: Int? = null,
        var answer: String? = null,
        var result: Boolean? = null
)