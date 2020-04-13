package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.model.PublishContent
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.service.CurrentContentService
import ie.daithi.quizmaster.service.GameService
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.CreateGame
import ie.daithi.quizmaster.web.model.QuestionPointer
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
        private val appUserRepo: AppUserRepo
){


    @GetMapping("/admin/game")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get Game", notes = "Get the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    @ResponseBody
    fun get(@RequestParam id: String): Game {
        return gameService.get(id)
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

    @PutMapping("/admin/game")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Create Game", notes = "Issues an email to all players with a link to allow them to access the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 502, message = "An error occurred when attempting to send email")
    )
    @ResponseBody
    fun put(@RequestBody createGame: CreateGame): Game {
        val id = SecurityContextHolder.getContext().authentication.name
        return gameService.create(id, createGame.playerEmails, createGame.quizId)
    }

    @DeleteMapping("/admin/game")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Delete Game", notes = "Delete the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Game not found")
    )
    fun delete(@RequestParam id: String) {
        return gameService.delete(id)
    }

    @PostMapping("/admin/game/publishQuestion")
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
    fun getCurrentContent(): PublishContent? {
        val id = SecurityContextHolder.getContext().authentication.name
        val appUser = appUserRepo.findByUsernameIgnoreCase(id) ?: throw NotFoundException("User not found")
        val game = gameService.getByPlayerId(appUser.id!!)
        return currentContentService.get(game.id!!)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}