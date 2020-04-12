package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AnswerRepo
import org.apache.logging.log4j.LogManager
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class AnswerService(
    private val answerRepo: AnswerRepo,
    private val gameService: GameService,
    private val scoringService: ScoringService,
    private val messageSender: SimpMessagingTemplate
) {

    fun submitAnswer(id: String, gameId: String, roundIndex: Int, questionIndex: Int, answer: String) {

        val answerObj = Answer(playerId = id, gameId = gameId, roundIndex = roundIndex, questionIndex = questionIndex, answer = answer)

        // 1. Attempt to correct
        val game = gameService.get(gameId)
        val question = gameService.getQuestion(game.quizId!!, roundIndex, questionIndex)
        scoringService.attemptScore(question!!.answer!!, answerObj, question.points)

        // 2. Store answer
        answerRepo.save(answerObj)

        // 3. Publish to Quiz Master if not able to correct
        if (answerObj.score == null)
            messageSender.convertAndSendToUser(game.quizMasterId!!, "/scoring", answerObj)
    }

    fun getUnscoredAnswers(gameId: String): List<Answer> {
        return answerRepo.findByGameIdAndScore(gameId = gameId, score = null)
    }

    fun save(answer: Answer) {
        answerRepo.save(answer)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}