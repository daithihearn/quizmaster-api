package ie.daithi.quizmaster.service

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import ie.daithi.quizmaster.validation.EmailValidator
import ie.daithi.quizmaster.web.exceptions.InvalidEmailException
import ie.daithi.quizmaster.web.exceptions.SendEmailException
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.io.IOException

@Service
@Profile("prod")
class SendGridEmailService(
        private val emailClient: SendGrid,
        private val emailValidator: EmailValidator,
        @Value("\${email.from.address}")
        private val fromAddress: String,
        @Value("\${player.login.url}")
        private val playerLoginUrl: String
): EmailService {

    override fun sendQuizInvite(recipientEmail: String, password: String) {
        if(!emailValidator.isValid(recipientEmail))
            throw InvalidEmailException("Invalid email: $recipientEmail")

        val from = Email(fromAddress)
        val subject = "Quiz!"
        val to = Email(recipientEmail)
        val content = Content("text/html", "<html>You have been invited to join a quiz.<br><br>Please visit " +
                "<a href='$playerLoginUrl'>$playerLoginUrl</a> and log in with the credentials below <br><br>" +
                "<ul><li>Username: $recipientEmail</li><li>Password: $password</li></ul></html>")
        val mail = Mail(from, subject, to, content)

        val request = Request()

        request.method = Method.POST
        request.endpoint = "mail/send"
        request.body = mail.build()
        val response: Response = emailClient.api(request)

        logger.info("Send email operation returned a statusCode of ${response.statusCode}")

        if (response.statusCode > 202)
            throw SendEmailException("An error occurred when attempting to send email to $recipientEmail")
    }

    companion object {
        private val logger = LogManager.getLogger(this::class.java)
    }

}