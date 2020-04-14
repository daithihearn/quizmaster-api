package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.service.AnswerService
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.QuestionAnswerWrapper
import ie.daithi.quizmaster.web.model.QuestionPointer
import ie.daithi.quizmaster.web.model.Score
import ie.daithi.quizmaster.web.model.SubmitAnswer
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
@Api(tags = ["Answer"], description = "Endpoints that relate to CRUD operations on answers")
class AnswerController(
        private val answerService: AnswerService
) {

    @PostMapping("/answer")
    @ResponseStatus(value = HttpStatus.OK)
    @Throws(NotFoundException::class)
    @ApiOperation(value = "Submit answer", notes = "Submit answer")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun submitAnswer(@RequestBody answer: SubmitAnswer) {
        val id = SecurityContextHolder.getContext().authentication.name
        answerService.submitAnswer(
                id = id,
                gameId = answer.gameId,
                roundIndex = answer.roundIndex,
                questionIndex = answer.questionIndex,
                answer = answer.answer)
    }

    @PutMapping("/admin/answer")
    @ResponseStatus(value = HttpStatus.OK)
    @Throws(NotFoundException::class)
    @ApiOperation(value = "Submit corrected answer", notes = "Submit corrected answer")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun submitCorrection(@RequestBody answer: Answer) {
        answerService.save(answer)
    }

    @GetMapping("/admin/answer/unscored")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get unscored answers", notes = "Get unscored answers for the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    @ResponseBody
    fun getUnscoredAnswers(@RequestParam id: String): List<QuestionAnswerWrapper> {
        return answerService.getUnscoredAnswers(id)
    }

    @GetMapping("/answer/leaderboard")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get leaderboard", notes = "Get leaderboard")
    fun getLeaderboard(@RequestParam id: String): List<Score> {
        return answerService.getLeaderboard(id)
    }

    @PutMapping("/admin/answer/publishLeaderboard")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Publish leaderboard", notes = "Publish leaderboard")
    fun publishLeaderboard(@RequestParam id: String) {
        answerService.publishLeaderboard(id)
    }
}
