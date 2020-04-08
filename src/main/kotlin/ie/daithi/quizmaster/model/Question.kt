package ie.daithi.quizmaster.model

import ie.daithi.quizmaster.enumeration.QuestionType

data class Question(
        val id: String,
        val type: QuestionType
)