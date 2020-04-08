package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.enumeration.QuestionType
import ie.daithi.quizmaster.model.Question
import org.springframework.stereotype.Service

@Service
class QuestionServiceImpl() : QuestionService {
    override fun getQuestion(id: String): Question {
        // TODO: Stubbed
        return Question(id, QuestionType.MULTIPLE_CHOICE)
    }
}