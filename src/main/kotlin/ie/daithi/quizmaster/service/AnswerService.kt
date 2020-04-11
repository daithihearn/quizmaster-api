package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AnswerRepo
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service

@Service
class AnswerService(
    private val answerRepo: AnswerRepo
) {

    fun submitAnswer(id: String, gameId: String, roundIndex: Int, questionIndex: Int, answer: String) {
        val answer = Answer(playerId = id, gameId = gameId, roundIndex = roundIndex, questionIndex = questionIndex, answer = answer)
        answerRepo.save(answer)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}