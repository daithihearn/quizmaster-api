package ie.daithi.quizmaster.web.exceptions

import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AnswerResubmissionException: Exception {

    constructor(message: String) : super(message) {
        logger.error(message)
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}