package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.model.PublishContent
import ie.daithi.quizmaster.service.AnswerService
import ie.daithi.quizmaster.service.AppUserService
import ie.daithi.quizmaster.service.CurrentContentService
import ie.daithi.quizmaster.service.GameService
import ie.daithi.quizmaster.web.exceptions.ForbiddenException
import ie.daithi.quizmaster.web.model.CreateGame
import ie.daithi.quizmaster.web.model.PresentQuestion
import ie.daithi.quizmaster.web.model.QuestionPointer
import ie.daithi.quizmaster.web.model.enums.PublishContentType
import ie.daithi.quizmaster.model.AppUser
import io.swagger.annotations.*
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
@Api(tags = ["Game"], description = "Endpoints that relate to CRUD operations on Games")
class GameController (
        private val gameService: GameService,
        private val currentContentService: CurrentContentService,
        private val appUserService: AppUserService,
        private val answerService: AnswerService
){


    @GetMapping("/admin/game")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get Game", notes = "Get the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    @ResponseBody
    fun get(@RequestParam gameId: String): Game {
        return gameService.get(gameId)
    }

    @GetMapping("/admin/game/all")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get all games", notes = "Get all games")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getAll(): List<Game> {
        return gameService.getAll()
    }

    @GetMapping("/admin/game/players/all")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get ALL Players", notes = "Get all players")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getAllPlayers(): List<AppUser> {

        return appUserService.getAllUsers()
    }

    @GetMapping("/game/players")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get Players", notes = "Get the players for this game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    @ResponseBody
    fun getPlayersForGame(@RequestParam gameId: String): List<AppUser> {
        // 1. Get current user ID
        val id = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Get Game
        val game = gameService.get(gameId)

        // 3. Check the player is in this game
        if (!game.players.contains(id) && game.quizMasterId != id) throw ForbiddenException("Can only get players if you are part of the game or are the quizmaster.")

        // 4. Get players
        return appUserService.getUsers(game.players)
    }

    @GetMapping("/game/active")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get all active games", notes = "Get all active games")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getMyActive(): List<Game> {
        // 1. Get current user ID
        val id = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Get active games for player
        return gameService.getMyActive(id)
    }

    @GetMapping("/admin/game/active")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get all active games", notes = "Get all active games")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getActiveGamesForAdmin(): List<Game> {
        // 1. Get current user ID
        val id = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Get active games for admin
        return gameService.getActiveGamesForQuizmaster(id)
    }

    @PutMapping("/admin/game")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Create Game", notes = "Start a new game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun put(@RequestBody createGame: CreateGame): Game {
        val id = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")
        return gameService.create(id, createGame.name, createGame.players, createGame.quizId)
    }

    @PutMapping("/admin/game/addPlayer")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Add player", notes = "Adds player to the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 502, message = "An error occurred when attempting to send email")
    )
    fun addPlayer(@RequestParam gameId: String, @RequestParam playerEmail: String) {
        gameService.addPlayer(gameId, playerEmail)
    }

    @DeleteMapping("/admin/game/removePlayer")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Remove player from game", notes = "Remove player from game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 502, message = "An error occurred when attempting to send email")
    )
    fun removePlayer(gameId: String, playerId: String) {
        return gameService.removePlayer(gameId, playerId)
    }

    @PutMapping("/admin/game/finish")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Finish a Game", notes = "Finishes the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    @ResponseBody
    fun finish(@RequestParam gameId: String) {
        return gameService.finish(gameId)
    }

    @PutMapping("/admin/game/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Cancel a Game", notes = "Cancels the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    @ResponseBody
    fun cancel(@RequestParam gameId: String) {
        return gameService.cancel(gameId)
    }

    @DeleteMapping("/admin/game")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Delete Game", notes = "Delete the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    fun delete(@RequestParam gameId: String) {
        return gameService.delete(gameId)
    }

    @PutMapping("/admin/game/publishQuestion")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Publish question", notes = "Push question to screen")
    fun publishQuestion(@RequestBody pointer: QuestionPointer) {
        gameService.publishQuestion(pointer)
    }

    @GetMapping("/game/currentContent")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get current content", notes = "Get current content")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getCurrentContent(@RequestParam gameId: String): PublishContent? {

        // 1. Get current user ID
        val id = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Get Game
        val game = gameService.get(gameId)

        // 3. Check the player is in this game
        if (!game.players.contains(id)) throw ForbiddenException("Can only get current content if you are part of the game.")

        val content = currentContentService.get(game.id!!)?: return null

        // If the content type is a question check if they have already answered it
        if (content.type == PublishContentType.QUESTION
                && content.content is PresentQuestion
                && content.content != null
                && answerService.hasAnswered(game.id!!, id, (content.content as PresentQuestion).roundId, (content.content as PresentQuestion).questionId))
            return null

        return content
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}