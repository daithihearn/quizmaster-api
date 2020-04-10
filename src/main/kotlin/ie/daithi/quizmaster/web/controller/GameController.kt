package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.service.GameService
import ie.daithi.quizmaster.web.CreateGame
import io.swagger.annotations.*
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/game")
@Api(tags = ["Game"], description = "Endpoints that relate to CRUD operations on Games")
class GameController (
        private val gameService: GameService
){

    @PostMapping("/create")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Create Game", notes = "Issues an email to all players with a link to allow them to access the game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 502, message = "An error occurred when attempting to send email")
    )
    @ResponseBody
    fun startQuiz(@RequestBody createGame: CreateGame): Game {
        return gameService.create(createGame.playerEmails, createGame.quizId)
    }

    @PostMapping("/pushMessageToScreen")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Push message to screen", notes = "Push message to screen")
    fun pushMessageToScreen(@RequestBody message: String) {
        gameService.pushMessage(message)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}