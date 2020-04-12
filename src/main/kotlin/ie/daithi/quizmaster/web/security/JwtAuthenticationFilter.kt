package ie.daithi.quizmaster.web.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC512
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.security.exception.AuthenticationException
import ie.daithi.quizmaster.web.security.model.AppUser
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class JwtAuthenticationFilter(
        authManager: AuthenticationManager,
        private val appUsersRepository: AppUserRepo,
        private val secret: String,
        private val expiry: Long) : UsernamePasswordAuthenticationFilter() {

    init {
        authenticationManager = authManager
    }

    override fun attemptAuthentication(req: HttpServletRequest,
                              res: HttpServletResponse): Authentication {
        try {
            val user = jacksonObjectMapper().readValue(req.inputStream, AppUser::class.java)

            val appUser = user.username?.let { appUsersRepository.findByUsernameIgnoreCase(it) }

            val authorities = arrayListOf<SimpleGrantedAuthority>()

            if (appUser == null)
                throw NotFoundException("Appuser not found!")

            appUser.authorities?.forEach { authority -> authorities.add(SimpleGrantedAuthority(authority.toString())) }

            return authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            appUser.username,
                            user.password,
                            authorities))

        } catch (e: IOException) {
            throw AuthenticationException("Failed to authenticate", e)
        }

    }

    override fun successfulAuthentication(req: HttpServletRequest,
                                           res: HttpServletResponse,
                                           chain: FilterChain,
                                           auth: Authentication) {

        val authorities = arrayListOf<String>()
        auth.authorities.stream().forEach { authority -> authorities.add(authority.authority) }
        val authoritiesStr = arrayOfNulls<String>(authorities.size)
        authorities.toArray(authoritiesStr)

        // TODO: Reading SECRET from a constants file. Need a more secure way of doing this. HSM?
        val token = JWT.create()
                .withSubject((auth.principal as User).username)
                .withArrayClaim(SecurityConstants.CLAIM_STRING, authoritiesStr)
                .withExpiresAt(Date(System.currentTimeMillis() + expiry))
                .sign(HMAC512(secret.toByteArray()))
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
    }
}
