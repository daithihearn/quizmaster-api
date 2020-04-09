package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.service.AppUserService
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import io.swagger.annotations.*
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/session")
@Api(tags = ["Session"], description = "Endpoints for session info")
class SessionController (
        private val appUserService: AppUserService
){

    @GetMapping("/isLoggedIn")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "In the user logged in", notes = "Is the user logged in")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun isLoggedIn(): Boolean {
        return true
    }

    @GetMapping("/name")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get name", notes = "Get logged in user's name")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun name(authentication: Authentication): String {
        return appUserService.loadUserByUsername(authentication.name).username
    }
}