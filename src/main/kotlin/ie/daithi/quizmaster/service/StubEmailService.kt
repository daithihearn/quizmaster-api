package ie.daithi.quizmaster.service

import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("dev")
class StubEmailService(): EmailService {
    override fun sendQuizInvite(recipientEmail: String, password: String) {
        logger.warn("As you are in dev mode, email will not send.")
        logger.warn("Logging user credentials instead: Username: $recipientEmail Password: $password")
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }
}