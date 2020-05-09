package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.enumeration.AnswerMethod
import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.service.AnswerService
import ie.daithi.quizmaster.service.GameService
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.*
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
@Api(tags = ["Answer"], description = "Endpoints that relate to CRUD operations on answers")
class AnswerController(
        private val answerService: AnswerService,
        private val gameService: GameService,
        private val appUserRepo: AppUserRepo
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
                playerId = id,
                gameId = answer.gameId,
                roundId = answer.roundId,
                questionId = answer.questionId,
                answer = answer.answer)
    }

    @GetMapping("/answer/all")
    @ResponseStatus(value = HttpStatus.OK)
    @Throws(NotFoundException::class)
    @ApiOperation(value = "Get all my answers", notes = "Get all my answers for this game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getAllAnswers(): List<Answer> {
        val id = SecurityContextHolder.getContext().authentication.name
        val appUser = appUserRepo.findByUsernameIgnoreCase(id) ?: throw NotFoundException("User not found")
        val game = gameService.getByPlayerId(appUser.id!!)
        return answerService.getAnswersForPlayer(gameId = game.id!!, playerId = id)
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
        answer.method = AnswerMethod.MANUAL
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

    @GetMapping("/admin/answer")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get answers", notes = "Get answers")
    fun getAnswers(@ApiParam(required = true) @RequestParam gameId: String,
                   @ApiParam(required = false) @RequestParam roundId: String?,
                   @ApiParam(required = false) @RequestParam playerId: String?): List<QuestionAnswerWrapper> {

        return if (roundId == null && playerId == null)
            answerService.getAnswers(gameId)
        else if (roundId == null && playerId != null)
            answerService.getQuestionsAndAnswersForPlayer(gameId, playerId)
        else if (playerId == null && roundId != null)
            answerService.getAnswers(gameId, roundId)
        else
            answerService.getAnswers(gameId, roundId!!, playerId!!)
    }

    @GetMapping("/answer/leaderboard")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get leaderboard", notes = "Get leaderboard")
    fun getLeaderboard(@RequestParam gameId: String, @RequestParam roundId: String?): Leaderboard {

        if (roundId != null) return answerService.getLeaderboard(gameId, roundId)
        return answerService.getLeaderboard(gameId)
    }

    @PutMapping("/admin/answer/publishLeaderboard")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Publish leaderboard", notes = "Publish leaderboard")
    fun publishLeaderboard(@RequestParam gameId: String, @RequestParam roundId: String?) {
        answerService.publishLeaderboard(gameId, roundId)
    }

    @PutMapping("/admin/answer/publishAnswersForRound")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Publish answers for round", notes = "Publish answers for round")
    fun publishAnswersForRound(@RequestParam gameId: String, @RequestParam roundId: String) {
        answerService.publishAnswersForRound(gameId, roundId)
    }
}
