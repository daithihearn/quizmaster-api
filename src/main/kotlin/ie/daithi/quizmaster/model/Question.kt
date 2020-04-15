package ie.daithi.quizmaster.model

import ie.daithi.quizmaster.enumeration.QuestionType

data class Question(
        val id: String,
        val question: String,
        val imageUri: String? = null,
        val type: QuestionType? = null,
        val answer: String,
        val options: List<String>? = null,
        val points: Float? = null
)