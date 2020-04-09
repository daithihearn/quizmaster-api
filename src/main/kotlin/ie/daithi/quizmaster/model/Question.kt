package ie.daithi.quizmaster.model

import ie.daithi.quizmaster.enumeration.QuestionType

data class Question(
        var value: String? = null,
        var type: QuestionType? = null,
        var answer: String? = null,
        var options: List<String>? = null
)