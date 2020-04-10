package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Game
import ie.daithi.quizmaster.model.Player
import ie.daithi.quizmaster.model.Quiz
import ie.daithi.quizmaster.repositories.AppUserRepo
import ie.daithi.quizmaster.repositories.GameRepo
import ie.daithi.quizmaster.repositories.QuizRepo
import ie.daithi.quizmaster.validation.EmailValidator
import ie.daithi.quizmaster.web.exceptions.InvalidEmailException
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import ie.daithi.quizmaster.web.security.model.AppUser
import ie.daithi.quizmaster.web.security.model.Authority
import org.apache.logging.log4j.LogManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*

@Service
class QuizService(
        private val quizRepo: QuizRepo,
        private val gameRepo: GameRepo,
        private val emailValidator: EmailValidator,
        private val emailService: EmailService,
        private val appUserRepo: AppUserRepo,
        private val passwordEncoder: BCryptPasswordEncoder
) {

    fun get(id: String): Quiz {
        val quizOpt = quizRepo.findById(id)
        if (!quizOpt.isPresent)
            throw NotFoundException("Quiz with ID($id) not found")
        return quizOpt.get()
    }

    fun getAll(): List<Quiz> {
        return quizRepo.findAll()
    }

    fun save(quiz: Quiz): String {
        logger.info("Attempting to save a quiz: ${quiz.name}")
        quizRepo.save(quiz)
        logger.info("Quiz successfully saved: ${quiz.id}")
        return quiz.id!!
    }

    fun delete(id: String) {
        quizRepo.deleteById(id)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
        private val secureRandom = SecureRandom()
    }

}