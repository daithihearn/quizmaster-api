package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.enumeration.AnswerMethod
import ie.daithi.quizmaster.model.Answer
import org.apache.commons.text.similarity.FuzzyScore
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class ScoringService(
        @Value("\${scoring.threshold.lower}")
        private val lowerThreshold: Float,
        @Value("\${scoring.threshold.upper}")
        private val upperThreshold: Float
) {

    fun isCorrectAnswer(correctAnswer: String, answer: String): Boolean {
        // 1. Determine a winning score
        val winningScore = correctAnswer.length * upperThreshold

        // 2. Get Fuzzy match score
        val score = fuzzyScore.fuzzyScore(correctAnswer, answer)
        logger.debug("A winning score is $winningScore\nScored correctAnswer($correctAnswer) answer($answer): $score ")

        // 3. Return answer
        return score >= winningScore
    }

    fun isInCorrectAnswer(correctAnswer: String, answer: String): Boolean {
        // 1. Determine a loosing score
        val loosingScore = correctAnswer.length * lowerThreshold

        // 2. Get Fuzzy match score
        val score = fuzzyScore.fuzzyScore(correctAnswer, answer)
        logger.debug("A loosing score is $loosingScore\nScored correctAnswer($correctAnswer) answer($answer): $score ")

        // 3. Return answer
        return score <= loosingScore
    }

    fun attemptScore(answer: String, answerObj: Answer, points: Int?) {
        if (isCorrectAnswer(answer, answerObj.answer)) {
            answerObj.score = points ?: 1
            answerObj.method = AnswerMethod.AUTOMATIC
        } else if (lowerThreshold > 0 && isInCorrectAnswer(answer, answerObj.answer)) {
            answerObj.score = 0
            answerObj.method = AnswerMethod.AUTOMATIC
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
        private val fuzzyScore = FuzzyScore(Locale.ENGLISH)
    }

}