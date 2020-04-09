package ie.daithi.quizmaster.service

import ie.daithi.quizmaster.model.Quiz
import ie.daithi.quizmaster.repositories.QuizRepo
import ie.daithi.quizmaster.web.exceptions.NotFoundException
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

    fun save(quiz: Quiz) {
        quizRepo.save(quiz)
    }

    fun delete(id: String) {
        quizRepo.deleteById(id)
    }

}