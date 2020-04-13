package ie.daithi.quizmaster.service

import org.junit.Assert.*
import org.junit.Test

class ScoringServiceTest {

    @Test
    fun isValidAnswer_Positive1() {
        val currentAnswer = "abc"
        val answer = "abc"

        val response = scoringService.isCorrectAnswer(currentAnswer, answer)

        assertTrue(response)
    }

    @Test
    fun isValidAnswer_Positive2() {
        val currentAnswer = "AbC"
        val answer = "abc"

        val response = scoringService.isCorrectAnswer(currentAnswer, answer)

        assertTrue(response)
    }


    @Test
    fun isValidAnswer_Positive3() {
        val currentAnswer = "AbCdkjsfbkdjafkjrgbrigbwifwiudsisd,frehgerahfiuhfkhsdjkfhdskjfweifewuif"
        val answer = "AbCdkjsfbkdjafkjrgbrigbwifwiudsisd,frehgerahfiuhfkhsdj"

        val response = scoringService.isCorrectAnswer(currentAnswer, answer)

        assertTrue(response)
    }

    @Test
    fun isValidAnswer_Positive4() {
        val currentAnswer = "John Carpenter"
        val answer = "Jon carpnter"

        val response = scoringService.isCorrectAnswer(currentAnswer, answer)

        assertTrue(response)
    }

//    @Test
//    fun isValidAnswer_Positive5() {
//        val currentAnswer = "Goldie Hawn"
//        val answer = "Goldy Han"
//
//        val response = scoringService.isCorrectAnswer(currentAnswer, answer)
//
//        assertTrue(response)
//    }

    @Test
    fun isValidAnswer_Negative1() {
        val currentAnswer = "Goldie Hawn"
        val answer = "George Mark"

        val response = scoringService.isCorrectAnswer(currentAnswer, answer)

        assertFalse(response)
    }

//    @Test
//    fun isValidAnswer_Positive6() {
//        val currentAnswer = "Mount Kilimanjaro"
//        val answer = "Kilimanjaro"
//
//        val response = scoringService.isCorrectAnswer(currentAnswer, answer)
//
//        assertTrue(response)
//    }

    companion object {
        val scoringService = ScoringService()
    }

}