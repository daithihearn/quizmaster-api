package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import org.apache.logging.log4j.LogManager
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class AppUserService (
        private val appUserRepo: AppUserRepo
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val appUserOpt = appUserRepo.findById(username)
        if (!appUserOpt.isPresent)
            throw NotFoundException("User $username not found")
        val appUser = appUserOpt.get()

        val authorities = arrayListOf<GrantedAuthority>()
        appUser.authorities!!.forEach { authority -> authorities.add(SimpleGrantedAuthority(authority.toString())) }

        return User(appUser.id, appUser.password, authorities)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}