package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.repositories.AppUserRepo
import io.swagger.annotations.*
import org.apache.logging.log4j.LogManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/session")
@Api(tags = ["Session"], description = "Endpoints for session info")
class SessionController (
        private val appUserRepo: AppUserRepo
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
    fun name(): String? {
        return appUserRepo.findByIdOrNull(SecurityContextHolder.getContext().authentication.name)?.username
    }

    @GetMapping("/type")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get user type", notes = "Get user type")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun authorities(): String {
        val id = SecurityContextHolder.getContext().authentication.name
        logger.debug("Trying to get the user type for $id")

        val appUser = appUserRepo.findByIdOrNull(id) ?: return ""

        val authorities = appUser.authorities ?: return ""

        logger.debug("User type: ${authorities[0].name}")
        return authorities[0].name
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}