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

    fun isAbsoluteAnswer(correctAnswer: String, answer: String): Boolean {
        return correctAnswer.equals(answer, true)
    }

    fun isInCorrectAnswer(correctAnswer: String, answer: String): Boolean {
        // 1. Determine a loosing score
        val losingScore = correctAnswer.length * lowerThreshold

        // 2. Get Fuzzy match score
        val score = fuzzyScore.fuzzyScore(correctAnswer, answer)
        logger.debug("A losing score is $losingScore\nScored correctAnswer($correctAnswer) answer($answer): $score ")

        // 3. Return answer
        return score <= losingScore
    }

    fun attemptScore(answer: String, answerObj: Answer, points: Int?, absolute: Boolean) {
        if (absolute) {
            attemptAbsoluteScore(answer, answerObj, points)
        } else {
            attemptFuzzyScore(answer, answerObj, points)
        }
    }

    private fun attemptAbsoluteScore(answer: String, answerObj: Answer, points: Int?) {
        if (isAbsoluteAnswer(answer, answerObj.answer)) {
            answerObj.score = points ?: 1
            answerObj.method = AnswerMethod.AUTOMATIC
        } else {
            answerObj.score = 0
            answerObj.method = AnswerMethod.AUTOMATIC
        }
    }

    private fun attemptFuzzyScore(answer: String, answerObj: Answer, points: Int?) {
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