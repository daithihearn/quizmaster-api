package ie.daithi.quizmaster.service

import com.fasterxml.jackson.databind.ObjectMapper
import ie.daithi.quizmaster.model.PublishContent
import ie.daithi.quizmaster.web.model.enums.PublishContentType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage

@Service
class PublishService(
        private val messageSender: SimpMessagingTemplate,
        private val objectMapper: ObjectMapper,
        private val currentContentService: CurrentContentService
) {
    fun publishContent(recipients: List<String>, topic: String, content: Any, gameId: String, contentType: PublishContentType) {
        val contentWrapped = PublishContent(gameId = gameId, type = contentType, content = content)
        currentContentService.save(contentWrapped)
        publishContent(recipients, topic, contentWrapped)
    }

    fun publishContent(recipients: List<String>, topic: String, content: PublishContent) {
        val wsMessage = TextMessage(objectMapper.writeValueAsString(content))
        recipients.forEach {
            messageSender.convertAndSendToUser(it, topic, wsMessage)
        }
    }

    fun publishContent(recipient: String, topic: String, content: Any, gameId: String, contentType: PublishContentType) {
        publishContent(listOf(recipient), topic, content, gameId, contentType)
    }
}