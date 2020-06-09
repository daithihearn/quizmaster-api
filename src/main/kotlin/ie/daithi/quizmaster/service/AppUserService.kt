package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.model.AppUser
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service

@Service
class AppUserService (
        private val appUserRepo: AppUserRepo
) {

    fun getUser(userId: String): AppUser {
        val appUser = appUserRepo.findById(userId)
        if (appUser.isEmpty) throw NotFoundException("AppUser($userId) not found")
        return appUser.get()
    }

    fun getUsers(players: List<String>): List<AppUser> {
        return appUserRepo.findByIdIn(players)
    }

    fun getAllUsers(): List<AppUser> {
        return appUserRepo.findAll()
    }

    fun exists(id: String): Boolean {
        return appUserRepo.existsById(id)
    }

    fun updateUser(appUser: AppUser) {
        appUserRepo.save(appUser)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}