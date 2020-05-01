package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.model.Quiz
import ie.daithi.quizmaster.service.QuizService
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.DataWrapper
import io.swagger.annotations.*
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/quiz")
@Api(tags = ["Quiz"], description = "Endpoints that relate to CRUD operations on Quizzes")
class QuizController (
        private val quizService: QuizService
){

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    @Throws(NotFoundException::class)
    @ApiOperation(value = "Get Quiz", notes = "Get a quiz by it's ID")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful"),
            ApiResponse(code = 404, message = "Couldn't find Quiz with this ID")
    )
    @ResponseBody
    fun getQuiz(@ApiParam(required = true, value = "The unique ID for a quiz")
                @RequestParam(required = true) id: String): Quiz {
        return quizService.get(id)
    }

    @GetMapping("/all")
    @ResponseStatus(value = HttpStatus.OK)
    @Throws(NotFoundException::class)
    @ApiOperation(value = "Get all quizzes", notes = "Get all quizzes")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getAllQuizzes(): List<Quiz> {
        return quizService.getAll()
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    @Throws(NotFoundException::class)
    @ApiOperation(value = "Save quiz", notes = "Save quiz")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun putQuiz(@RequestBody quiz: Quiz): String {
        return quizService.save(quiz)
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Delete Quiz", notes = "Delete a quiz by it's ID")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    fun deleteQuiz(@ApiParam(required = true, value = "The unique ID for a quiz")
                   @RequestParam(required = true) id: String) {
        quizService.delete(id)
    }

    @PostMapping("/uploadImage")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Upload Media", notes = "Upload media content")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun uploadImage(@RequestBody media: DataWrapper): String {
        return quizService.uploadImage(media.data)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}