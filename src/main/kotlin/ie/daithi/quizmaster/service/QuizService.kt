package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Quiz
import ie.daithi.quizmaster.model.Round
import ie.daithi.quizmaster.repositories.QuizRepo
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service

@Service
class QuizService(
        private val quizRepo: QuizRepo
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
    }

}