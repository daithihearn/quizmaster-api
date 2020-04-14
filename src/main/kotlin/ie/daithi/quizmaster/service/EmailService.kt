package ie.daithi.quizmaster.service

interface EmailService {
    fun sendQuizInvite(recipientEmail: String, password: String)
}