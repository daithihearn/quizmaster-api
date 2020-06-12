package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.enumeration.AnswerMethod
import ie.daithi.quizmaster.model.Answer
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.service.AnswerService
import ie.daithi.quizmaster.service.GameService
import ie.daithi.quizmaster.web.exceptions.ForbiddenException
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
        private val gameService: GameService
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
        // 1. Get current user ID
        val id = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Get Game
        val game = gameService.get(answer.gameId)

        // 3. Check the player is in this game
        if (!game.players.contains(id)) throw ForbiddenException("Can only get answers if you are part of the game.")
        answerService.submitAnswer(
                playerId = id,
                game = game,
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
    fun getAllAnswers(@RequestParam gameId: String): List<Answer> {
        // 1. Get current user ID
        val id = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Get Game
        val game = gameService.get(gameId)

        // 3. Check the player is in this game
        if (!game.players.contains(id)) throw ForbiddenException("Can only get answers if you are part of the game.")
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

    @GetMapping("/answer/answered")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get who has answered a question", notes = "Get who has answered a question")
    fun getWhoHasAnswered(@ApiParam(required = true) @RequestParam gameId: String,
                   @ApiParam(required = true) @RequestParam roundId: String,
                   @ApiParam(required = true) @RequestParam questionId: String): List<String> {

        return answerService.getHasAnswered(gameId, roundId, questionId)
    }

    @GetMapping("/answer/leaderboard")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get leaderboard", notes = "Get leaderboard")
    fun getLeaderboard(@RequestParam gameId: String, @RequestParam roundId: String?): Leaderboard {

        if (roundId != null) return answerService.getLeaderboard(gameId, roundId)
        return answerService.getLeaderboard(gameId)
    }
}
