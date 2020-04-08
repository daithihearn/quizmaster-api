package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.model.Question
import ie.daithi.quizmaster.service.QuestionService
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import io.swagger.annotations.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
@Api(tags = ["Question"], description = "Endpoints that relate to Questions")
class QuestionController (private val questionService: QuestionService) {

    @GetMapping("/question")
    @ResponseStatus(value = HttpStatus.OK)
    @Throws(NotFoundException::class)
    @ApiOperation(value = "Get Question", notes = "Get a question by it's ID")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Couldn't find Question with this ID")
    )
    @ResponseBody
    fun getQuestion(@ApiParam(required = true, value = "The unique ID for a question")
        @RequestParam(required = true) id: String): Question {
        logger.debug("Getting question for ID: $id")
        return questionService.getQuestion(id)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(QuestionController)
    }

}