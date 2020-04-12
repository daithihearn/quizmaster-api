package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AnswerRepo
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service

@Service
class AnswerService(
    private val answerRepo: AnswerRepo
) {

    fun submitAnswer(id: String, roundIndex: Int, questionIndex: Int, answer: String) {
        answerRepo.save(Answer(playerId = id, roundIndex = roundIndex, questionIndex = questionIndex, answer = answer))
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}