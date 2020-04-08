package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Question

interface QuestionService {
    fun getQuestion(id: String): Question
}