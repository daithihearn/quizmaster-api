package ie.daithi.quizmaster.web.controller

import ie.daithi.quizmaster.service.AppUserService
import ie.daithi.quizmaster.web.exceptions.ForbiddenException
import ie.daithi.quizmaster.model.AppUser
import ie.daithi.quizmaster.web.model.UpdateProfile
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
@Api(tags = ["Profile"], description = "Endpoints that relate to CRUD operations on Profiles")
class ProfileController(
        private val appUserService: AppUserService
) {

    @GetMapping("/profile/has")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "User has profile?", notes = "Does the user have a profile already?")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun hasProfile(): Boolean {
        // 1. Get current user ID
        val subject = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Check if a record exists
        return appUserService.existsBySubject(subject)
    }

    @GetMapping("/profile")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Get the profile", notes = "Get the user's current profile")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    @ResponseBody
    fun getProfile(): AppUser {
        // 1. Get current user ID
        val subject = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Check if a record exists
        return appUserService.getUserBySubject(subject)
    }

    @PutMapping("/profile")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Update the profile", notes = "Update the user's current profile")
    @ApiResponses(
            ApiResponse(code = 200, message = "Request successful")
    )
    fun updateProfile(@RequestBody updateProfile: UpdateProfile) {
        // 1. Get current user ID
        val subject = SecurityContextHolder.getContext().authentication.name ?: throw ForbiddenException("Couldn't authenticate user")

        // 2. Check if a record exists
        return appUserService.updateUser(subject = subject,
                name = updateProfile.name,
                email = updateProfile.email.trim().toLowerCase(),
                picture = updateProfile.picture)
    }
}