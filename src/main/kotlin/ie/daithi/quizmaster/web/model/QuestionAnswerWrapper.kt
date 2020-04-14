package ie.daithi.quizmaster.web.model

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.model.Question

data class QuestionAnswerWrapper(
        val question: Question,
        val answer: Answer
)