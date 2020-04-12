package ie.daithi.quizmaster.model

import ie.daithi.quizmaster.enumeration.QuestionType

data class Question(
        var index: Int? = null,
        var value: String? = null,
        var imageUri: String? = null,
        var type: QuestionType? = null,
        var answer: String? = null,
        var options: List<String>? = null
)