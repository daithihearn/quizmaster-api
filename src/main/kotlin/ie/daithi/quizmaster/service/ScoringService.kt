package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Answer
import org.apache.commons.text.similarity.FuzzyScore
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import java.util.*

@Service
class ScoringService {

    fun isCorrectAnswer(correctAnswer: String, answer: String): Boolean {
        // 1. Determine a winning score
        val winningScore = correctAnswer.length * 2

        // 2. Get Fuzzy match score
        val score = fuzzyScore.fuzzyScore(correctAnswer, answer)
        logger.debug("A winning score is $winningScore\nScored correctAnswer($correctAnswer) answer($answer): $score ")

        // 3. Return answer
        return score >= winningScore
    }

    fun isInCorrectAnswer(correctAnswer: String, answer: String): Boolean {
        // 1. Determine a loosing score
        val loosingScore = correctAnswer.length * 0.4

        // 2. Get Fuzzy match score
        val score = fuzzyScore.fuzzyScore(correctAnswer, answer)
        logger.debug("A loosing score is $loosingScore\nScored correctAnswer($correctAnswer) answer($answer): $score ")

        // 3. Return answer
        return score <= loosingScore
    }

    fun attemptScore(answer: String, answerObj: Answer, points: Float?) {
        if (isCorrectAnswer(answer, answerObj.answer)) {
            answerObj.score = points ?: 1f
        } else if (isInCorrectAnswer(answer, answerObj.answer)) {
            answerObj.score = 0f
        }
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
        private val fuzzyScore = FuzzyScore(Locale.ENGLISH)
    }

}