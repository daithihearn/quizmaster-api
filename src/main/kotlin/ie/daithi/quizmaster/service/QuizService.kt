package ie.daithi.quizmaster.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import ie.daithi.quizmaster.model.Quiz
import ie.daithi.quizmaster.repositories.QuizRepo
import ie.daithi.quizmaster.web.exceptions.NotFoundException
import org.apache.commons.codec.digest.DigestUtils
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import java.net.URLDecoder

@Service
class QuizService(
        private val quizRepo: QuizRepo,
        private val cloudinary: Cloudinary
) {

    fun get(id: String): Quiz {
        val quizOpt = quizRepo.findById(id)
        if (!quizOpt.isPresent)
            throw NotFoundException("Quiz with ID($id) not found")
        return quizOpt.get()
    }

    fun getAll(): List<Quiz> {
        return quizRepo.getAll()
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

    fun uploadImage(media: String): String {

        val publicId = "quizzes/images/${DigestUtils.md5Hex(media)}"

        // Can we check if it already exists here?

        val params = ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "image"
        )

        return cloudinary.uploader().upload(media, params)["secure_url"] as String
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}