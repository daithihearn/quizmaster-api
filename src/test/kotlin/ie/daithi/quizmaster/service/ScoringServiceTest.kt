package ie.daithi.quizmaster.service

import org.junit.Assert.*
import org.junit.Test

class ScoringServiceTest {

    @Test
    fun isValidAnswer_Positive1() {
        val question = "abc"
        val answer = "abc"

        val response = scoringService.isCorrectAnswer(question, answer)

        assertTrue(response)
    }

    @Test
    fun isValidAnswer_Positive2() {
        val question = "AbC"
        val answer = "abc"

        val response = scoringService.isCorrectAnswer(question, answer)

        assertTrue(response)
    }


    @Test
    fun isValidAnswer_Positive3() {
        val question = "AbCdkjsfbkdjafkjrgbrigbwifwiudsisd,frehgerahfiuhfkhsdjkfhdskjfweifewuif"
        val answer = "AbCdkjsfbkdjafkjrgbrigbwifwiudsisd,frehgerahfiuhfkhsdj"

        val response = scoringService.isCorrectAnswer(question, answer)

        assertTrue(response)
    }

    @Test
    fun isValidAnswer_Positive4() {
        val question = "John Carpenter"
        val answer = "Jon carpnter"

        val response = scoringService.isCorrectAnswer(question, answer)

        assertTrue(response)
    }

//    @Test
//    fun isValidAnswer_Positive5() {
//        val question = "Goldie Hawn"
//        val answer = "Goldy Han"
//
//        val response = scoringService.isCorrectAnswer(question, answer)
//
//        assertTrue(response)
//    }

    @Test
    fun isValidAnswer_Negative1() {
        val question = "Goldie Hawn"
        val answer = "George Mark"

        val response = scoringService.isCorrectAnswer(question, answer)

        assertFalse(response)
    }

    @Test
    fun isValidAnswer_Positive6() {
        val question = "Mount Kilimanjaro"
        val answer = "Kilimanjaro"

        val response = scoringService.isCorrectAnswer(question, answer)

        assertTrue(response)
    }

    companion object {
        val scoringService = ScoringService()
    }

}