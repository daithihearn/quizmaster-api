package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.service.AnswerService
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.model.SubmitAnswer
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/answer")
@Api(tags = ["Answer"], description = "Endpoints that relate to CRUD operations on answers")
class AnswerController(
        private val answerService: AnswerService
) {

    @PostMapping
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
                roundIndex = answer.roundIndex,
                questionIndex = answer.questionIndex,
                answer = answer.answer)
    }
}
